package com.orion.ops.handler.app.machine;

import com.orion.exception.LogException;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.handler.app.action.IActionHandler;
import com.orion.ops.handler.tail.ITailHandler;
import com.orion.ops.handler.tail.TailSessionHolder;
import com.orion.remote.channel.SessionStore;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.Threads;
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

    protected SessionStore sessionStore;

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
                        if (!terminated) {
                            ex = e;
                        }
                    }
                } else {
                    // 跳过
                    handler.skipped();
                }
            }
        } catch (Exception e) {
            // 异常
            ex = e;
            isMainError = true;
        } finally {
            // 执行完成回调
            this.handlerFinishCallback(ex, isMainError);
        }
        // 更新状态
        if (terminated) {
            this.updateStatus(MachineProcessorStatus.TERMINATED);
            this.appendFinishedLog(null, false);
        } else {
            this.updateStatus(ex == null ? MachineProcessorStatus.FINISH : MachineProcessorStatus.FAILURE);
            this.appendFinishedLog(ex, isMainError);
        }
        // 关闭
        Streams.close(this);
    }

    /**
     * 检查是否可执行
     *
     * @return 是否可执行
     */
    protected abstract boolean checkCanRunnable();

    /**
     * 执行器执行完毕回调
     *
     * @param ex          e
     * @param isMainError 是否是主进程执行错误
     */
    protected void handlerFinishCallback(Exception ex, boolean isMainError) {
        if (ex == null) {
            log.info("机器任务执行-成功 relId: {}", id);
        } else {
            log.error("机器任务执行-失败 relId: {}, relId: {}", id, isMainError, ex);
        }
    }

    /**
     * 打开日志
     */
    protected void openLogger() {
        String logPath = Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), this.getLogPath());
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
     * 拼接完成日志
     *
     * @param ex          ex
     * @param isMainError isMainError
     */
    protected void appendFinishedLog(Exception ex, boolean isMainError) {
        StringBuilder log = new StringBuilder();
        if (ex != null) {
            // 有异常
            log.append("# 机器操作执行失败 结束时间: ").append(Dates.format(endTime))
                    .append("; used: ").append(endTime.getTime() - startTime.getTime())
                    .append("ms\n");
        } else {
            // 无异常
            if (terminated) {
                log.append("# 机器操作手动停止 结束时间: ").append(Dates.format(endTime)).append(Const.LF);
            } else {
                log.append("# 机器操作执行完成 结束时间: ").append(Dates.format(endTime))
                        .append("; used: ").append(endTime.getTime() - startTime.getTime())
                        .append("ms\n");
            }
        }
        // 拼接日志
        this.appendLog(log.toString());
        // 拼接异常
        if (ex != null && isMainError) {
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
        logStream.write(log.getBytes());
    }

    @Override
    public void terminated() {
        // 设置状态为已停止
        this.terminated = true;
        // 更新状态
        this.updateStatus(MachineProcessorStatus.TERMINATED);
        // 结束正在执行的action
        for (IActionHandler handler : handlerList) {
            if (ActionStatus.RUNNABLE.equals(handler.getStatus())) {
                handler.terminated();
            }
        }
    }

    @Override
    public void close() {
        // 关闭日志流
        Streams.close(logStream);
        // 关闭handler
        Lists.stream(handlerList).forEach(Streams::close);
        // 关闭session
        Streams.close(sessionStore);
        // 异步关闭正在tail的日志
        Threads.start(() -> {
            try {
                Threads.sleep(Const.MS_S_10);
                tailSessionHolder.getSession(Const.HOST_MACHINE_ID, logAbsolutePath).forEach(ITailHandler::close);
            } catch (Exception e) {
                log.error("MachineProcessor-关闭tail失败 {} {}", id, e);
                e.printStackTrace();
            }
        });
    }

}
