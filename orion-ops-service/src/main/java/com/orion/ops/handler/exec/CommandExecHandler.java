package com.orion.ops.handler.exec;

import com.orion.constant.Letters;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.command.ExecStatus;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.CommandExecDAO;
import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.handler.tail.TailSessionHolder;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.ssh.CommandExecutor;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.Threads;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;

/**
 * 命令执行器 基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/7 17:17
 */
@Slf4j
public class CommandExecHandler implements IExecHandler {

    private static CommandExecDAO commandExecDAO = SpringHolder.getBean(CommandExecDAO.class);

    private static MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    private static ExecSessionHolder execSessionHolder = SpringHolder.getBean(ExecSessionHolder.class);

    private static TailSessionHolder tailSessionHolder = SpringHolder.getBean(TailSessionHolder.class);

    private Long execId;

    private CommandExecDO record;

    private MachineInfoDO machine;

    private SessionStore sessionStore;

    private CommandExecutor executor;

    private int exitCode;

    private String logPath;

    private OutputStream logOutputStream;

    private boolean terminated;

    protected CommandExecHandler(Long execId) {
        this.execId = execId;
    }

    @Override
    public void exec() {
        log.info("execHandler-提交 execId: {}", execId);
        Threads.start(this, SchedulerPools.EXEC_SCHEDULER);
    }

    @Override
    public void run() {
        log.info("execHandler-执行开始 execId: {}", execId);
        // 获取执行数据
        this.getExecData();
        // 检查状态
        if (record == null || !ExecStatus.WAITING.getStatus().equals(record.getExecStatus())) {
            return;
        }
        // 执行
        Exception ex = null;
        try {
            // 更新状态
            this.updateStatus(ExecStatus.RUNNABLE);
            execSessionHolder.addSession(execId, this);
            // 打开日志
            this.openLogger();
            // 打开executor
            this.sessionStore = machineInfoService.openSessionStore(machine);
            this.executor = sessionStore.getCommandExecutor(Strings.replaceCRLF(record.getExecCommand()));
            // 开始执行
            executor.inherit()
                    .sync()
                    .transfer(logOutputStream)
                    .connect()
                    .exec();
        } catch (Exception e) {
            ex = e;
        }
        // 回调
        try {
            if (terminated) {
                // 停止回调
                log.info("execHandler-执行停止 execId: {}", execId);
                this.terminatedCallback();
            } else if (ex == null) {
                // 完成回调
                this.completeCallback();
            } else {
                // 失败回调
                log.error("execHandler-执行失败 execId: {}", execId, ex);
                this.exceptionCallback(ex);
            }
        } finally {
            // 释放资源
            this.close();
        }
    }

    /**
     * 获取执行数据
     */
    private void getExecData() {
        this.record = commandExecDAO.selectById(execId);
        if (record == null) {
            return;
        }
        if (!ExecStatus.WAITING.getStatus().equals(record.getExecStatus())) {
            return;
        }
        // 查询机器信息
        this.machine = machineInfoService.selectById(record.getMachineId());
        // 设置日志信息
        File logFile = new File(Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), record.getLogPath()));
        Files1.touch(logFile);
        this.logPath = logFile.getAbsolutePath();
    }

    @Override
    public void write(String out) {
        executor.write(out);
    }

    @Override
    public void terminated() {
        log.info("execHandler-停止 execId: {}", execId);
        this.terminated = true;
        Streams.close(executor);
    }

    /**
     * 打开日志
     */
    @SneakyThrows
    private void openLogger() {
        // 打开日志流
        log.info("execHandler-打开日志流 {} {}", execId, logPath);
        File logFile = new File(logPath);
        this.logOutputStream = Files1.openOutputStreamFast(logFile);
        StringBuilder sb = new StringBuilder()
                .append("# 准备执行命令\n")
                .append("@ssh: ").append(machine.getUsername()).append("@")
                .append(machine.getMachineHost()).append(":")
                .append(machine.getSshPort()).append(Letters.LF)
                .append("执行用户: ").append(record.getUserId()).append(Letters.TAB)
                .append(record.getUserName()).append(Letters.LF)
                .append("执行任务: ").append(execId).append(Letters.LF)
                .append("执行机器: ").append(machine.getId()).append(Letters.TAB)
                .append(machine.getMachineName()).append(Letters.LF)
                .append("开始时间: ").append(Dates.format(record.getStartDate()))
                .append(Letters.LF);
        String description = record.getDescription();
        if (!Strings.isBlank(description)) {
            sb.append("描述: ").append(description).append(Letters.LF);
        }
        sb.append(Letters.LF)
                .append("# 执行命令\n")
                .append(record.getExecCommand()).append(Letters.LF)
                .append(Letters.LF)
                .append("# 开始执行\n");
        logOutputStream.write(Strings.bytes(sb.toString()));
        logOutputStream.flush();
    }

    /**
     * 停止回调
     */
    private void terminatedCallback() {
        // 更新状态
        this.updateStatus(ExecStatus.TERMINATED);
        // 拼接日志
        this.appendLog("\n# 命令执行停止\n");
    }

    /**
     * 完成回调
     */
    private void completeCallback() {
        this.exitCode = executor.getExitCode();
        log.info("execHandler-执行完成 execId: {} exitCode: {}", execId, exitCode);
        // 更新状态
        this.updateStatus(ExecStatus.COMPLETE);
        // 拼接日志
        Date endDate = new Date();
        String interval = Dates.interval(endDate, record.getStartDate(), "d", "h", "m", "s");
        StringBuilder sb = new StringBuilder()
                .append(Letters.LF)
                .append("# 命令执行完毕\n")
                .append("exit code: ").append(exitCode).append(Letters.LF)
                .append("结束时间: ").append(Dates.format(endDate))
                .append("; used ").append(interval).append(" (")
                .append(endDate.getTime() - record.getStartDate().getTime())
                .append(" ms)\n");
        this.appendLog(sb.toString());
    }

    /**
     * 异常回调
     *
     * @param e e
     */
    private void exceptionCallback(Exception e) {
        // 更新状态
        this.updateStatus(ExecStatus.EXCEPTION);
        // 拼接日志
        StringBuilder log = new StringBuilder("\n--------------------------------------------------\n# 命令执行异常\n")
                .append(Exceptions.getStackTraceAsString(e))
                .append(Const.LF);
        this.appendLog(log.toString());
    }

    @SneakyThrows
    private void appendLog(String log) {
        logOutputStream.write(Strings.bytes(log));
    }

    /**
     * 更新状态
     *
     * @param status status
     */
    private void updateStatus(ExecStatus status) {
        Date now = new Date();
        // 更新
        CommandExecDO update = new CommandExecDO();
        update.setId(execId);
        update.setExecStatus(status.getStatus());
        update.setUpdateTime(now);
        switch (status) {
            case RUNNABLE:
                record.setStartDate(now);
                update.setStartDate(now);
                break;
            case COMPLETE:
                update.setEndDate(now);
                update.setExitCode(exitCode);
                break;
            case EXCEPTION:
            case TERMINATED:
                update.setEndDate(now);
                break;
            default:
        }
        int effect = commandExecDAO.updateById(update);
        log.info("execHandler-更新状态 id: {}, status: {}, effect: {}", execId, status, effect);
    }

    @Override
    public void close() {
        log.info("execHandler-关闭 id: {}", execId);
        // 移除会话
        execSessionHolder.removeSession(execId);
        // 释放资源
        Streams.close(executor);
        Streams.close(sessionStore);
        Streams.close(logOutputStream);
        // 异步关闭正在tail的日志
        tailSessionHolder.asyncCloseTailFile(Const.HOST_MACHINE_ID, logPath);
    }

}
