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
package com.orion.ops.handler.app.action;

import com.orion.lang.constant.Letters;
import com.orion.lang.define.io.OutputAppender;
import com.orion.lang.exception.ExecuteException;
import com.orion.lang.exception.LogException;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.io.Streams;
import com.orion.lang.utils.time.Dates;
import com.orion.net.remote.ExitCode;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.app.ActionStatus;
import com.orion.ops.constant.app.ActionType;
import com.orion.ops.constant.common.StainCode;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.dao.ApplicationActionLogDAO;
import com.orion.ops.entity.domain.ApplicationActionLogDO;
import com.orion.ops.utils.Utils;
import com.orion.spring.SpringHolder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.util.Date;

/**
 * 应用操作处理器基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/11 15:27
 */
@Slf4j
public abstract class AbstractActionHandler implements IActionHandler {

    protected static ApplicationActionLogDAO applicationActionLogDAO = SpringHolder.getBean(ApplicationActionLogDAO.class);

    protected Long id;

    protected Long relId;

    protected MachineActionStore store;

    protected ApplicationActionLogDO action;

    protected OutputStream outputSteam;

    protected OutputAppender appender;

    protected volatile boolean terminated;

    protected Date startTime, endTime;

    @Getter
    protected volatile ActionStatus status;

    public AbstractActionHandler(Long id, MachineActionStore store) {
        this.id = id;
        this.relId = store.getRelId();
        this.store = store;
        this.action = store.getActions().get(id);
        this.status = ActionStatus.of(action.getRunStatus());
    }

    @Override
    public void exec() {
        log.info("应用操作执行-开始: relId: {}, id: {}", relId, id);
        // 状态检查
        if (!ActionStatus.WAIT.equals(status)) {
            return;
        }
        Exception ex = null;
        // 执行
        try {
            // 更新状态
            this.updateStatus(ActionStatus.RUNNABLE);
            // 打开日志
            this.openLogger();
            // 执行
            this.handler();
        } catch (Exception e) {
            ex = e;
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
                // 异常回调
                this.exceptionCallback(ex);
                throw Exceptions.runtime(ex.getMessage(), ex);
            }
        } finally {
            // 释放资源
            this.close();
        }
    }

    /**
     * 处理流程
     *
     * @throws Exception Exception
     */
    protected abstract void handler() throws Exception;

    @Override
    public void skip() {
        log.info("应用操作执行-跳过: relId: {}, id: {}", relId, id);
        if (ActionStatus.WAIT.equals(status)) {
            // 只能跳过等待中的任务
            this.updateStatus(ActionStatus.SKIPPED);
        }
    }

    @Override
    public void terminate() {
        log.info("应用操作执行-终止: relId: {}, id: {}", relId, id);
        this.terminated = true;
    }

    /**
     * 打开日志
     */
    protected void openLogger() {
        String logPath = Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), action.getLogPath());
        log.info("应用操作执行-打开日志 relId: {}, id: {}, path: {}", relId, id, logPath);
        File logFile = new File(logPath);
        Files1.touch(logFile);
        this.outputSteam = Files1.openOutputStreamFastSafe(logFile);
        this.appender = OutputAppender.create(outputSteam).then(store.getSuperLogStream());
        // 拼接开始日志
        this.appendStartedLog();
    }

    /**
     * 拼接开始日志
     */
    @SneakyThrows
    private void appendStartedLog() {
        StringBuilder log = new StringBuilder()
                .append(StainCode.prefix(StainCode.GLOSS_GREEN))
                .append("# ").append(action.getActionName()).append(" 执行开始")
                .append(StainCode.SUFFIX)
                .append(Letters.TAB)
                .append(Utils.getStainKeyWords(Dates.format(startTime), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        ActionType actionType = ActionType.of(action.getActionType());
        if (ActionType.BUILD_COMMAND.equals(actionType) || ActionType.RELEASE_COMMAND.equals(actionType)) {
            log.append(Letters.LF)
                    .append(Utils.getStainKeyWords("# 执行命令", StainCode.GLOSS_BLUE))
                    .append(Letters.LF)
                    .append(StainCode.prefix(StainCode.GLOSS_CYAN))
                    .append(Utils.getEndLfWithEof(action.getActionCommand()))
                    .append(StainCode.SUFFIX);
        }
        store.getSuperLogStream().write(Strings.bytes(Const.LF_3));
        this.appendLog(log.toString());
    }

    /**
     * 停止回调
     */
    private void terminatedCallback() {
        log.info("应用操作执行-终止回调: relId: {}, id: {}", relId, id);
        // 修改状态
        this.updateStatus(ActionStatus.TERMINATED);
        // 拼接日志
        StringBuilder log = new StringBuilder(Const.LF)
                .append(StainCode.prefix(StainCode.GLOSS_YELLOW))
                .append("# ").append(action.getActionName()).append(" 手动停止")
                .append(StainCode.SUFFIX)
                .append(Letters.TAB)
                .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
                .append(Const.LF);
        this.appendLog(log.toString());
    }

    /**
     * 成功回调
     */
    private void successCallback() {
        log.info("应用操作执行-成功回调: relId: {}, id: {}", relId, id);
        // 修改状态
        this.updateStatus(ActionStatus.FINISH);
        // 拼接完成日志
        this.appendFinishedLog(null);
    }

    /**
     * 异常回调
     *
     * @param ex ex
     */
    private void exceptionCallback(Exception ex) {
        log.error("应用操作执行-异常回调: relId: {}, id: {}", relId, id, ex);
        // 修改状态
        this.updateStatus(ActionStatus.FAILURE);
        // 拼接完成日志
        this.appendFinishedLog(ex);
    }

    /**
     * 拼接完成日志
     *
     * @param ex ex
     */
    private void appendFinishedLog(Exception ex) {
        StringBuilder log = new StringBuilder();
        Integer actionType = action.getActionType();
        if (ActionType.BUILD_COMMAND.getType().equals(actionType) || ActionType.RELEASE_COMMAND.getType().equals(actionType)) {
            log.append(Const.LF);
        }
        if (ex != null) {
            // 有异常
            log.append(StainCode.prefix(StainCode.GLOSS_RED))
                    .append("# ").append(action.getActionName()).append(" 执行失败")
                    .append(StainCode.SUFFIX)
                    .append(Letters.TAB)
                    .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE));
            Integer exitCode = this.getExitCode();
            if (exitCode != null) {
                log.append("  exitcode: ")
                        .append(ExitCode.isSuccess(exitCode)
                                ? Utils.getStainKeyWords(exitCode, StainCode.GLOSS_BLUE)
                                : Utils.getStainKeyWords(exitCode, StainCode.GLOSS_RED));
            }
            log.append(Letters.LF);
        } else {
            // 无异常
            long used = endTime.getTime() - startTime.getTime();
            log.append(StainCode.prefix(StainCode.GLOSS_GREEN))
                    .append("# ").append(action.getActionName()).append(" 执行完成")
                    .append(StainCode.SUFFIX)
                    .append(Letters.TAB)
                    .append(Utils.getStainKeyWords(Dates.format(endTime), StainCode.GLOSS_BLUE))
                    .append(" used ")
                    .append(Utils.getStainKeyWords(Utils.interval(used), StainCode.GLOSS_BLUE))
                    .append(" (")
                    .append(StainCode.prefix(StainCode.GLOSS_BLUE))
                    .append(used)
                    .append("ms")
                    .append(StainCode.SUFFIX)
                    .append(")\n");
        }
        // 拼接异常
        if (ex != null && !(ex instanceof ExecuteException)) {
            log.append(Const.LF);
            if (ex instanceof LogException) {
                log.append(Utils.getStainKeyWords(ex.getMessage(), StainCode.GLOSS_RED));
            } else {
                log.append(Exceptions.getStackTraceAsString(ex));
            }
            log.append(Const.LF);
        }
        // 拼接日志
        this.appendLog(log.toString());
    }

    /**
     * 拼接日志
     *
     * @param log log
     */
    @SneakyThrows
    protected void appendLog(String log) {
        appender.write(Strings.bytes(log));
        appender.flush();
    }

    /**
     * 更新状态
     *
     * @param status status
     */
    protected void updateStatus(ActionStatus status) {
        Date now = new Date();
        this.status = status;
        ApplicationActionLogDO update = new ApplicationActionLogDO();
        update.setId(id);
        update.setRunStatus(status.getStatus());
        update.setUpdateTime(now);
        switch (status) {
            case RUNNABLE:
                this.startTime = now;
                update.setStartTime(now);
                break;
            case FINISH:
            case FAILURE:
            case TERMINATED:
                this.endTime = now;
                update.setEndTime(now);
                update.setExitCode(this.getExitCode());
                break;
            default:
                break;
        }
        // 更新状态
        applicationActionLogDAO.updateById(update);
    }

    @Override
    public void close() {
        // 关闭日志
        Streams.close(outputSteam);
    }

}
