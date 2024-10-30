/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.handler.app.machine;

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.app.ActionStatus;
import cn.orionsec.ops.constant.common.StainCode;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.handler.app.action.IActionHandler;
import cn.orionsec.ops.handler.tail.TailSessionHolder;
import cn.orionsec.ops.utils.Utils;
import com.orion.lang.constant.Letters;
import com.orion.lang.exception.DisabledException;
import com.orion.lang.exception.LogException;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.io.Streams;
import com.orion.lang.utils.time.Dates;
import com.orion.spring.SpringHolder;
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

    private static final TailSessionHolder tailSessionHolder = SpringHolder.getBean(TailSessionHolder.class);

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
            this.completeCallback(ex);
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
            } else if (ex instanceof DisabledException) {
                // 机器未启用回调
                this.machineDisableCallback();
            } else {
                // 执行失败回调
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
     *
     * @param e e
     */
    protected void completeCallback(Exception e) {
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
        StringBuilder log = new StringBuilder(Const.LF_2)
                .append(Utils.getStainKeyWords("# 主机任务手动停止", StainCode.GLOSS_YELLOW))
                .append(Letters.TAB)
                .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
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
        long used = endTime.getTime() - startTime.getTime();
        // 拼接日志
        StringBuilder log = new StringBuilder(Const.LF_2)
                .append(Utils.getStainKeyWords("# 主机任务执行完成", StainCode.GLOSS_GREEN))
                .append(Const.TAB)
                .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
                .append(" used ")
                .append(Utils.getStainKeyWords(Utils.interval(used), StainCode.GLOSS_BLUE))
                .append(" (")
                .append(StainCode.prefix(StainCode.GLOSS_BLUE))
                .append(used)
                .append("ms")
                .append(StainCode.SUFFIX)
                .append(")\n");
        // 拼接日志
        this.appendLog(log.toString());
    }

    /**
     * 机器未启用回调
     */
    private void machineDisableCallback() {
        log.info("机器任务执行-机器未启用 relId: {}", id);
        // 更新状态
        this.updateStatus(MachineProcessorStatus.TERMINATED);
        // 拼接日志
        StringBuilder log = new StringBuilder(Const.LF_2)
                .append(Utils.getStainKeyWords("# 主机任务执行机器未启用", StainCode.GLOSS_YELLOW))
                .append(Letters.TAB)
                .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
                .append(Const.LF);
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
        StringBuilder log = new StringBuilder(Const.LF_2)
                .append(Utils.getStainKeyWords("# 主机任务执行失败", StainCode.GLOSS_RED))
                .append(Letters.TAB)
                .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
                .append(Letters.LF);
        // 拼接异常
        if (isMainError) {
            log.append(Const.LF);
            if (ex instanceof LogException) {
                log.append(Utils.getStainKeyWords(ex.getMessage(), StainCode.GLOSS_RED));
            } else {
                log.append(Exceptions.getStackTraceAsString(ex));
            }
            log.append(Const.LF);
        }
        this.appendLog(log.toString());
    }

    /**
     * 拼接日志
     *
     * @param log log
     */
    @SneakyThrows
    protected void appendLog(String log) {
        logStream.write(Strings.bytes(log));
        logStream.flush();
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
    public void write(String command) {
        Lists.stream(handlerList)
                .filter(s -> ActionStatus.RUNNABLE.equals(s.getStatus()))
                .forEach(s -> s.write(command));
    }

    @Override
    public void close() {
        // 关闭日志流
        Streams.close(logStream);
        // 异步关闭正在tail的日志
        tailSessionHolder.asyncCloseTailFile(Const.HOST_MACHINE_ID, logAbsolutePath);
    }

}
