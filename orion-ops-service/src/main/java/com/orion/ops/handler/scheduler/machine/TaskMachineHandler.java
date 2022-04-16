package com.orion.ops.handler.scheduler.machine;

import com.orion.ops.consts.Const;
import com.orion.ops.consts.scheduler.SchedulerTaskMachineStatus;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.SchedulerTaskMachineRecordDAO;
import com.orion.ops.entity.domain.SchedulerTaskMachineRecordDO;
import com.orion.ops.handler.tail.TailSessionHolder;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.remote.ExitCode;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.ssh.CommandExecutor;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;

/**
 * 任务机器操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/24 22:22
 */
@Slf4j
public class TaskMachineHandler implements ITaskMachineHandler {

    private static SchedulerTaskMachineRecordDAO schedulerTaskMachineRecordDAO = SpringHolder.getBean(SchedulerTaskMachineRecordDAO.class);

    private static MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    private static TailSessionHolder tailSessionHolder = SpringHolder.getBean(TailSessionHolder.class);

    private String logPath;

    private OutputStream logOutputStream;

    private SessionStore sessionStore;

    private CommandExecutor executor;

    private Long machineRecordId;

    private SchedulerTaskMachineRecordDO machineRecord;

    private Date startTime, endTime;

    private Integer exitCode;

    private volatile boolean terminated;

    @Getter
    private volatile SchedulerTaskMachineStatus status;

    public TaskMachineHandler(Long machineRecordId) {
        this.machineRecordId = machineRecordId;
        this.status = SchedulerTaskMachineStatus.WAIT;
    }

    @Override
    public void run() {
        // 检查状态
        log.info("调度任务-机器操作-开始 machineRecordId: {}", machineRecordId);
        this.machineRecord = schedulerTaskMachineRecordDAO.selectById(machineRecordId);
        this.status = SchedulerTaskMachineStatus.of(machineRecord.getExecStatus());
        if (!SchedulerTaskMachineStatus.WAIT.equals(status)) {
            return;
        }
        // 执行
        Exception ex = null;
        try {
            this.updateStatus(SchedulerTaskMachineStatus.RUNNABLE);
            // 打开日志
            this.openLogger();
            // 打开机器
            this.sessionStore = machineInfoService.openSessionStore(machineRecord.getMachineId());
            // 获取执行器
            this.executor = sessionStore.getCommandExecutor(Strings.replaceCRLF(machineRecord.getExecCommand()));
            // 开始执行
            executor.inherit()
                    .sync()
                    .transfer(logOutputStream)
                    .connect()
                    .exec();
            this.exitCode = executor.getExitCode();
        } catch (Exception e) {
            ex = e;
        }
        // 回调
        try {
            if (terminated) {
                // 停止回调
                this.terminatedCallback();
            } else if (ex == null) {
                // 完成回调
                this.completeCallback();
            } else {
                // 异常回调
                this.exceptionCallback(ex);
                throw Exceptions.runtime(ex);
            }
        } finally {
            // 释放资源
            this.close();
        }
    }

    /**
     * 停止回调
     */
    private void terminatedCallback() {
        log.error("调度任务-机器操作-停止 machineRecordId: {}", machineRecordId);
        // 更新状态
        this.updateStatus(SchedulerTaskMachineStatus.TERMINATED);
        // 拼接日志
        StringBuilder log = new StringBuilder()
                .append("\n调度任务执行停止\n结束时间: ")
                .append(Dates.format(endTime))
                .append("; used: ").append(endTime.getTime() - startTime.getTime())
                .append("ms\n");
        this.appendLog(log.toString());
    }

    /**
     * 完成回调
     */
    private void completeCallback() {
        log.info("调度任务-机器操作-完成 machineRecordId: {}, exitCode: {}", machineRecordId, exitCode);
        final boolean execSuccess = ExitCode.SUCCESS.getCode().equals(exitCode);
        // 更新状态
        if (execSuccess) {
            this.updateStatus(SchedulerTaskMachineStatus.SUCCESS);
        } else {
            this.updateStatus(SchedulerTaskMachineStatus.FAILURE);
        }
        // 拼接日志
        StringBuilder log = new StringBuilder()
                .append("\n调度任务执行完成\n结束时间: ")
                .append(Dates.format(endTime))
                .append("; used: ").append(endTime.getTime() - startTime.getTime())
                .append("ms\n");
        log.append("执行结果: ")
                .append(execSuccess ? "成功" : "失败")
                .append(" exitCode: ")
                .append(exitCode)
                .append(Const.LF);
        this.appendLog(log.toString());
    }

    /**
     * 异常回调
     */
    private void exceptionCallback(Exception e) {
        log.error("调度任务-机器操作-失败 machineRecordId: {}", machineRecordId, e);
        // 更新状态
        this.updateStatus(SchedulerTaskMachineStatus.FAILURE);
        // 拼接日志
        StringBuilder log = new StringBuilder()
                .append("\n调度任务执行失败\n结束时间: ")
                .append(Dates.format(endTime))
                .append("; used: ").append(endTime.getTime() - startTime.getTime())
                .append("ms\n")
                .append(Exceptions.getStackTraceAsString(e));
        this.appendLog(log.toString());
    }

    @Override
    public void skipped() {
        log.info("调度任务-机器操作-跳过 machineRecordId: {}, status: {}", machineRecordId, status);
        if (SchedulerTaskMachineStatus.WAIT.equals(status)) {
            // 只能跳过等待中的任务
            this.updateStatus(SchedulerTaskMachineStatus.SKIPPED);
        }
    }

    @Override
    public void terminated() {
        log.info("调度任务-机器操作-停止 machineRecordId: {}", machineRecordId);
        // 只能停止进行中的任务
        if (SchedulerTaskMachineStatus.RUNNABLE.equals(status)) {
            this.terminated = true;
            Streams.close(this.executor);
        }
    }

    /**
     * 打开日志
     */
    private void openLogger() {
        File logFile = new File(Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), machineRecord.getLogPath()));
        Files1.touch(logFile);
        this.logPath = logFile.getAbsolutePath();
        // 打开日志流
        log.info("TaskMachineHandler-打开日志流 {} {}", machineRecordId, logPath);
        this.logOutputStream = Files1.openOutputStreamFastSafe(logFile);
        // 拼接开始日志
        StringBuilder log = new StringBuilder()
                .append("开始执行调度任务 ")
                .append(machineRecord.getMachineName())
                .append(Const.LF)
                .append("开始时间: ")
                .append(Dates.format(startTime))
                .append(Const.LF);
        log.append("执行命令:\n")
                .append(machineRecord.getExecCommand())
                .append(Const.LF)
                .append(Const.LF);
        this.appendLog(log.toString());
    }

    /**
     * 拼接日志
     *
     * @param log log
     */
    @SneakyThrows
    private void appendLog(String log) {
        logOutputStream.write(Strings.bytes(log));
    }

    /**
     * 更新状态
     *
     * @param status status
     */
    private void updateStatus(SchedulerTaskMachineStatus status) {
        Date now = new Date();
        this.status = status;
        SchedulerTaskMachineRecordDO update = new SchedulerTaskMachineRecordDO();
        update.setId(machineRecordId);
        update.setExecStatus(status.getStatus());
        update.setUpdateTime(now);
        switch (status) {
            case RUNNABLE:
                this.startTime = now;
                update.setStartTime(now);
                break;
            case SUCCESS:
            case FAILURE:
            case TERMINATED:
                if (startTime != null) {
                    this.endTime = now;
                    update.setEndTime(now);
                    update.setExitCode(exitCode);
                }
                break;
            default:
        }
        schedulerTaskMachineRecordDAO.updateById(update);
    }

    @Override
    public void close() {
        // 释放资源
        Streams.close(executor);
        Streams.close(sessionStore);
        Streams.close(logOutputStream);
        // 异步关闭正在tail的日志
        tailSessionHolder.asyncCloseTailFile(Const.HOST_MACHINE_ID, logPath);
    }

}
