package com.orion.ops.handler.exec;

import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.command.ExecStatus;
import com.orion.ops.dao.CommandExecDAO;
import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.ssh.BaseRemoteExecutor;
import com.orion.remote.channel.ssh.CommandExecutor;
import com.orion.spring.SpringHolder;
import com.orion.utils.Threads;
import com.orion.utils.io.Streams;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Date;

/**
 * 命令执行器 基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/7 17:17
 */
@Slf4j
public abstract class AbstractExecHandler implements IExecHandler {

    protected static CommandExecDAO commandExecDAO = SpringHolder.getBean(CommandExecDAO.class);

    protected static MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    protected static ExecSessionHolder execSessionHolder = SpringHolder.getBean(ExecSessionHolder.class);

    protected ExecHint hint;

    protected Long execId;

    protected CommandExecDO record;

    protected MachineInfoDO machine;

    protected SessionStore sessionStore;

    @Getter
    protected CommandExecutor executor;

    protected int exitCode;

    protected AbstractExecHandler(ExecHint hint) {
        this.hint = hint;
        this.record = hint.getRecord();
        this.execId = record.getId();
        this.machine = hint.getMachine();
    }

    @Override
    public void exec() {
        log.info("execHandler-执行命令-提交 machineId: {}, type: {}, command: {}", hint.getMachineId(), hint.getExecType(), record.getExecCommand());
        Threads.start(this, SchedulerPools.EXEC_SCHEDULER);
    }

    @Override
    public void run() {
        // 检查状态
        CommandExecDO current = commandExecDAO.selectById(execId);
        if (!ExecStatus.WAITING.getStatus().equals(current.getExecStatus())) {
            return;
        }
        try {
            // 更新状态
            this.updateStatus(ExecStatus.RUNNABLE);
            // 打开日志
            this.openLogger();
            // 打开executor
            this.sessionStore = machineInfoService.openSessionStore(machine);
            this.executor = sessionStore.getCommandExecutor(record.getExecCommand());
            execSessionHolder.addSession(execId, this);
            // 开始执行
            executor.inherit()
                    .sync()
                    .streamHandler(this::processCommandOutputStream)
                    .callback(this::callback)
                    .connect()
                    .exec();
        } catch (Exception e) {
            this.onException(e);
            throw e;
        } finally {
            this.close();
        }
    }

    @Override
    public void write(String out) {
        executor.write(out);
    }

    /**
     * 打开日志
     */
    protected abstract void openLogger();

    /**
     * 处理命令输出
     *
     * @param in in
     */
    protected abstract void processCommandOutputStream(InputStream in);

    /**
     * 完成回调
     *
     * @param executor executor
     */
    protected void callback(BaseRemoteExecutor executor) {
        this.exitCode = ((CommandExecutor) executor).getExitCode();
        log.info("execHandler-执行命令完成回调 execId: {} code: {}", execId, exitCode);
        this.updateStatus(ExecStatus.COMPLETE);
    }

    /**
     * 发生异常时调用
     *
     * @param e e
     */
    protected void onException(Exception e) {
        log.error("execHandler-执行命令失败 execId: {} {}", execId, e);
        e.printStackTrace();
        this.updateStatus(ExecStatus.EXCEPTION);
    }

    /**
     * 更新状态
     *
     * @param status status
     */
    protected void updateStatus(ExecStatus status) {
        CommandExecDO update = new CommandExecDO();
        update.setId(execId);
        update.setExecStatus(status.getStatus());
        record.setExecStatus(status.getStatus());
        if (ExecStatus.RUNNABLE.equals(status)) {
            Date startDate = new Date();
            update.setStartDate(startDate);
            record.setStartDate(startDate);
        } else if (ExecStatus.COMPLETE.equals(status)) {
            update.setExitCode(exitCode);
            record.setExitCode(exitCode);
        }
        int effect = commandExecDAO.updateById(update);
        log.info("execHandler-更新状态 id: {}, status: {}, effect: {}", execId, status, effect);
    }

    @Override
    public void close() {
        Date endDate = new Date();
        // 更新执行时间
        CommandExecDO update = new CommandExecDO();
        update.setId(execId);
        update.setEndDate(endDate);
        commandExecDAO.updateById(update);
        log.info("execHandler-关闭 id: {}, status: {}, exitCode: {} used {} ms", execId, record.getExecStatus(),
                exitCode, endDate.getTime() - record.getStartDate().getTime());
        // 释放资源
        Streams.close(executor);
        Streams.close(sessionStore);
        execSessionHolder.removeSession(execId);
    }

    public boolean isDone() {
        return executor.isDone();
    }

    public boolean isClose() {
        return executor.isClosed();
    }

}
