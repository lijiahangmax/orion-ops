package com.orion.ops.handler.release.handler;

import com.orion.constant.Letters;
import com.orion.exception.LogException;
import com.orion.lang.io.OutputAppender;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.ApplicationReleaseActionDAO;
import com.orion.ops.entity.domain.ApplicationReleaseActionDO;
import com.orion.ops.handler.release.ReleaseStore;
import com.orion.ops.handler.release.machine.MachineStore;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Date;

/**
 * 发布操作处理器基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/28 18:00
 */
@Slf4j
public abstract class AbstractReleaseHandler implements IReleaseHandler {

    protected static ApplicationReleaseActionDAO applicationReleaseActionDAO = SpringHolder.getBean(ApplicationReleaseActionDAO.class);

    protected Long releaseId;

    protected Long actionId;

    protected ApplicationReleaseActionDO action;

    protected ReleaseStore store;

    protected MachineStore machineStore;

    protected OutputAppender appender;

    @Getter
    protected volatile ActionStatus status;

    public AbstractReleaseHandler(Long actionId, ReleaseStore store, MachineStore machineStore) {
        this.actionId = actionId;
        this.store = store;
        this.machineStore = machineStore;
        this.action = machineStore.getActions().get(actionId);
        this.releaseId = action.getReleaseId();
        this.status = ActionStatus.WAIT;
    }

    @Override
    public void exec() {
        log.info("应用发布action-开始: releaseId: {}, actionId: {}", releaseId, actionId);
        Exception ex = null;
        try {
            // 更新状态
            this.updateStatus(ActionStatus.RUNNABLE);
            // 打开日志
            this.openLogger();
            // 执行
            this.handler();
        } catch (Exception e) {
            log.error("应用发布action-异常: releaseId: {}, actionId: {}", releaseId, actionId, e);
            ex = e;
        }
        if (ActionStatus.TERMINATED.getStatus().equals(action.getRunStatus())) {
            // 拼接日志
            this.appendTerminatedLog();
        } else {
            // 修改状态
            this.updateStatus(ex == null ? ActionStatus.FINISH : ActionStatus.FAILURE);
            // 拼接日志
            this.appendFinishedLog(ex);
            if (ex != null) {
                throw Exceptions.runtime(ex.getMessage(), ex);
            }
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
        log.info("应用发布action-跳过: releaseId: {}, actionId: {}", releaseId, actionId);
        this.updateStatus(ActionStatus.SKIPPED);
    }

    @Override
    public void terminated() {
        log.info("应用发布action-终止: releaseId: {}, actionId: {}", releaseId, actionId);
        this.updateStatus(ActionStatus.TERMINATED);
    }

    /**
     * 打开日志
     */
    protected void openLogger() {
        String logPath = Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), action.getLogPath());
        log.info("应用发布action-打开日志 releaseId: {}, actionId: {}, path: {}", releaseId, actionId, logPath);
        File logFile = new File(logPath);
        Files1.touch(logFile);
        this.appender = OutputAppender.create(Files1.openOutputStreamFastSafe(logFile))
                .then(machineStore.getLogStream())
                .onClose(true);
        // 拼接开始日志
        this.appendStartedLog();
    }

    /**
     * 拼接开始日志
     */
    @SneakyThrows
    private void appendStartedLog() {
        StringBuilder log = new StringBuilder()
                .append("# 执行发布操作 ").append(action.getActionName())
                .append(Const.LF);
        if (ActionType.RELEASE_COMMAND.equals(ActionType.of(action.getActionType()))) {
            log.append("# 执行命令: ").append(action.getActionCommand()).append("\n\n");
        }
        machineStore.getLogStream().write(Letters.LF);
        this.appendLog(log.toString());
    }

    /**
     * 拼接完成日志
     *
     * @param ex ex
     */
    private void appendFinishedLog(Exception ex) {
        StringBuilder log = new StringBuilder();
        if (ex != null) {
            // 有异常
            log.append("# 发布操作执行失败 ").append(action.getActionName());
            Integer exitCode = this.getExitCode();
            if (exitCode != null) {
                log.append("\texitCode: ").append(exitCode).append(";");
            }
        } else {
            // 无异常
            log.append("# 发布操作执行完成 ").append(action.getActionName());
        }
        log.append(" used: ")
                .append(action.getEndTime().getTime() - action.getStartTime().getTime())
                .append("ms\n");
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
     * 拼接停止日志
     */
    private void appendTerminatedLog() {
        StringBuilder log = new StringBuilder()
                .append("\n# 发布操作手动停止 ").append(action.getActionName())
                .append(" used: ").append(action.getEndTime().getTime() - action.getStartTime().getTime())
                .append("ms\n\n");
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
        appender.write(log.getBytes());
    }

    /**
     * 更新状态
     *
     * @param status status
     */
    protected void updateStatus(ActionStatus status) {
        ApplicationReleaseActionDO update = new ApplicationReleaseActionDO();
        update.setId(actionId);
        update.setRunStatus(status.getStatus());
        action.setRunStatus(status.getStatus());
        this.status = status;
        switch (status) {
            case RUNNABLE:
                update.setStartTime(new Date());
                action.setStartTime(update.getStartTime());
                break;
            case FINISH:
            case FAILURE:
                update.setEndTime(new Date());
                update.setExitCode(this.getExitCode());
                action.setEndTime(update.getEndTime());
                action.setExitCode(update.getExitCode());
                break;
            case TERMINATED:
                if (action.getStartTime() != null) {
                    update.setEndTime(new Date());
                    update.setExitCode(this.getExitCode());
                    action.setEndTime(update.getEndTime());
                    action.setExitCode(update.getExitCode());
                }
                break;
            case SKIPPED:
            default:
                break;
        }
        // 更新状态
        applicationReleaseActionDAO.updateById(update);
    }

    @Override
    public void close() {
        // 关闭日志
        Streams.close(appender);
    }

}
