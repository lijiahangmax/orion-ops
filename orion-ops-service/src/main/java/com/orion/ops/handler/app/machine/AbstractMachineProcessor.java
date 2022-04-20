package com.orion.ops.handler.app.machine;

import com.orion.exception.LogException;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.handler.app.action.IActionHandler;
import com.orion.ops.handler.tail.TailSessionHolder;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * 机器处理器基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/12 13:40
 */
@Slf4j
public abstract class AbstractMachineProcessor implements IMachineProcessor {

    private static TailSessionHolder tailSessionHolder = SpringHolder.getBean(TailSessionHolder.class);

    protected Long id;

    protected String logAbsolutePath;

    protected OutputStream logStream;

    protected Date startTime, endTime;

    /**
     * 处理器
     */
    protected List<IActionHandler> handlerList;

    /**
     * 是否已终止
     */
    protected volatile boolean terminated;

    public AbstractMachineProcessor(Long id) {
        this.id = id;
    }

    @Override
    public void run() {
        // 检查状态
        if (!this.checkCanRunnable()) {
            return;
        }
        Exception ex = null;
        boolean isMainError = false;
        // 执行
        try {
            // 更新状态
            this.updateStatus(MachineProcessorStatus.RUNNABLE);
            // 打开日志
            this.openLogger();
            // 打开机器连接
            this.openMachineSession();
            // 执行
            for (IActionHandler handler : handlerList) {
                if (ex == null && !terminated) {
                    try {
                        // 执行
                        handler.exec();
                    } catch (Exception e) {
                        // 强制停止的异常不算异常
                        if (!terminated) {
                            ex = e;
                        }
                    }
                } else {
                    // 跳过
                    handler.skip();
                }
            }
            // 完成回调
            this.completeCallback();
        } catch (Exception e) {
            // 异常
            ex = e;
            isMainError = true;
        }
        // 回调
        try {
            if (terminated) {
                // 停止回调
                this.terminatedCallback();
            } else if (ex == null) {
                // 成功回调
                this.successCallback();
            } else {
                // 失败回调
                this.exceptionCallback(isMainError, ex);
            }
        } finally {
            // 释放资源
            this.close();
        }
    }

    /**
     * 检查是否可执行
     *
     * @return 是否可执行
     */
    protected abstract boolean checkCanRunnable();

    /**
     * 打开日志
     */
    protected void openLogger() {
        String logPath = Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), this.getLogPath());
        File logFile = new File(logPath);
        Files1.touch(logFile);
        this.logStream = Files1.openOutputStreamFastSafe(logFile);
        this.logAbsolutePath = logFile.getAbsolutePath();
        // 拼接开始日志
        this.appendStartedLog();
    }

    /**
     * 获取日志文件路径
     *
     * @return logPath
     */
    protected abstract String getLogPath();

    /**
     * 打开session
     */
    protected abstract void openMachineSession();

    /**
     * 更新状态
     *
     * @param status status
     */
    protected abstract void updateStatus(MachineProcessorStatus status);

    /**
     * 拼接开始日志
     */
    protected abstract void appendStartedLog();

    /**
     * 完成回调
     */
    protected void completeCallback() {
        log.info("机器任务执行-完成 relId: {}", id);
    }

    /**
     * 停止回调
     */
    protected void terminatedCallback() {
        log.info("机器任务执行-停止 relId: {}", id);
        // 修改状态
        this.updateStatus(MachineProcessorStatus.TERMINATED);
        // 拼接日志
        StringBuilder log = new StringBuilder()
                .append("# 机器操作手动停止 结束时间: ")
                .append(Dates.format(endTime))
                .append(Const.LF);
        this.appendLog(log.toString());
    }

    /**
     * 成功回调
     */
    protected void successCallback() {
        log.info("机器任务执行-成功 relId: {}", id);
        // 修改状态
        this.updateStatus(MachineProcessorStatus.FINISH);
        // 拼接日志
        StringBuilder log = new StringBuilder()
                .append("# 机器操作执行完成 结束时间: ").append(Dates.format(endTime))
                .append("; used: ").append(endTime.getTime() - startTime.getTime())
                .append("ms\n");
        // 拼接日志
        this.appendLog(log.toString());
    }

    /**
     * 异常回调
     *
     * @param isMainError isMainError
     * @param ex          ex
     */
    protected void exceptionCallback(boolean isMainError, Exception ex) {
        log.error("机器任务执行-失败 relId: {}, isMainError: {}", id, isMainError, ex);
        // 更新状态
        this.updateStatus(MachineProcessorStatus.FAILURE);
        // 拼接日志
        StringBuilder log = new StringBuilder()
                .append("# 机器操作执行失败 结束时间: ").append(Dates.format(endTime))
                .append("; used: ").append(endTime.getTime() - startTime.getTime())
                .append("ms\n");
        this.appendLog(log.toString());
        // 拼接异常
        if (isMainError) {
            if (ex instanceof LogException) {
                this.appendLog(ex.getMessage() + Const.LF);
            } else {
                this.appendLog(Exceptions.getStackTraceAsString(ex) + Const.LF);
            }
        }
    }

    /**
     * 拼接日志
     *
     * @param log log
     */
    @SneakyThrows
    protected void appendLog(String log) {
        logStream.write(Strings.bytes(log));
    }

    @Override
    public void terminate() {
        // 设置状态为已停止
        this.terminated = true;
        // 结束正在执行的action
        Lists.stream(handlerList)
                .filter(s -> ActionStatus.RUNNABLE.equals(s.getStatus()))
                .forEach(IActionHandler::terminate);
    }

    @Override
    public void close() {
        // 关闭日志流
        Streams.close(logStream);
        // 异步关闭正在tail的日志
        tailSessionHolder.asyncCloseTailFile(Const.HOST_MACHINE_ID, logAbsolutePath);
    }

}
