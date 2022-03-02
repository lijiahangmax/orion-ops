package com.orion.ops.handler.app.action;

import com.orion.constant.Letters;
import com.orion.exception.LogException;
import com.orion.lang.io.OutputAppender;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.ApplicationActionLogDAO;
import com.orion.ops.entity.domain.ApplicationActionLogDO;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
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

    protected boolean terminated;

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
            log.error("应用操作执行-异常: relId: {}, id: {}", relId, id, e);
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
    public void skipped() {
        log.info("应用操作执行-跳过: relId: {}, id: {}", relId, id);
        if (ActionStatus.WAIT.equals(status)) {
            // 只能跳过等待中的任务
            this.updateStatus(ActionStatus.SKIPPED);
        }
    }

    @Override
    public void terminated() {
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
                .append("# 操作 [").append(action.getActionName()).append("] 执行开始\n");
        ActionType actionType = ActionType.of(action.getActionType());
        if (ActionType.BUILD_COMMAND.equals(actionType)
                || ActionType.RELEASE_COMMAND.equals(actionType)) {
            log.append("# 执行命令: ").append(action.getActionCommand()).append("\n\n");
        }
        store.getSuperLogStream().write(Letters.LF);
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
        StringBuilder log = new StringBuilder()
                .append("\n# 执行操作手动停止 ")
                .append(action.getActionName());
        log.append(" used: ")
                .append(endTime.getTime() - startTime.getTime())
                .append("ms\n\n");
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
        log.info("应用操作执行-异常回调: relId: {}, id: {}", relId, id);
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
        if (ActionType.BUILD_COMMAND.getType().equals(actionType)
                || ActionType.RELEASE_COMMAND.getType().equals(actionType)) {
            log.append(Const.LF);
        }
        if (ex != null) {
            // 有异常
            log.append("# 操作 [").append(action.getActionName()).append("] 执行失败");
            Integer exitCode = this.getExitCode();
            if (exitCode != null) {
                log.append("\texitCode: ").append(exitCode).append(";");
            }
        } else {
            // 无异常
            log.append("# 操作 [").append(action.getActionName()).append("] 执行完成");
        }
        log.append(" used: ").append(endTime.getTime() - startTime.getTime()).append("ms\n");
        // 拼接异常
        if (ex != null) {
            if (ex instanceof LogException) {
                log.append(ex.getMessage()).append(Const.LF);
            } else {
                log.append(Exceptions.getStackTraceAsString(ex)).append(Const.LF);
            }
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
