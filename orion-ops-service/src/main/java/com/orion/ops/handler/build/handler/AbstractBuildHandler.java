package com.orion.ops.handler.build.handler;

import com.orion.constant.Letters;
import com.orion.lang.io.OutputAppender;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.entity.domain.ApplicationBuildActionDO;
import com.orion.ops.handler.build.BuildStore;
import com.orion.ops.service.api.ApplicationBuildService;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.PrintStream;
import java.util.Date;

/**
 * 应用构建处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/6 8:53
 */
@Slf4j
public abstract class AbstractBuildHandler implements IBuildHandler {

    protected static ApplicationBuildService applicationBuildService = SpringHolder.getBean(ApplicationBuildService.class);

    protected Long buildId;

    protected Long actionId;

    protected BuildStore store;

    protected ApplicationBuildActionDO action;

    protected OutputAppender appender;

    @Getter
    protected volatile ActionStatus status;

    public AbstractBuildHandler(Long actionId, BuildStore store) {
        this.actionId = actionId;
        this.store = store;
        this.buildId = store.getBuildRecord().getId();
        this.action = store.getActions().get(actionId);
        this.status = ActionStatus.WAIT;
    }

    @Override
    public void exec() {
        log.info("应用构建action-开始: buildId: {}, actionId: {}", buildId, actionId);
        Exception ex = null;
        try {
            // 更新状态
            this.updateStatus(ActionStatus.RUNNABLE);
            // 打开日志
            this.openLogger();
            // 执行
            this.handler();
        } catch (Exception e) {
            log.error("应用构建action-异常: buildId: {}, actionId: {}", buildId, actionId, e);
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
     * 执行
     *
     * @throws Exception Exception
     */
    protected abstract void handler() throws Exception;

    @Override
    public void skipped() {
        log.info("应用构建action-跳过: buildId: {}, actionId: {}", buildId, actionId);
        this.updateStatus(ActionStatus.SKIPPED);
    }

    @Override
    public void terminated() {
        log.info("应用构建action-终止: buildId: {}, actionId: {}", buildId, actionId);
        this.updateStatus(ActionStatus.TERMINATED);
    }

    /**
     * 打开日志
     */
    protected void openLogger() {
        String logPath = Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), action.getLogPath());
        log.info("应用构建action-打开日志 buildId: {}, actionId: {}, path: {}", buildId, actionId, logPath);
        File logFile = new File(logPath);
        Files1.touch(logFile);
        this.appender = OutputAppender.create(Files1.openOutputStreamFastSafe(logFile))
                .then(store.getMainLogStream())
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
                .append("# 执行构建操作 ").append(action.getActionName())
                .append(Const.LF);
        if (ActionType.BUILD_HOST_COMMAND.equals(ActionType.of(action.getActionType()))) {
            log.append("# 执行命令: ").append(action.getActionCommand()).append("\n\n");
        }
        store.getMainLogStream().write(Letters.LF);
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
            log.append("# 构建操作执行失败 ").append(action.getActionName());
            Integer exitCode = this.getExitCode();
            if (exitCode != null) {
                log.append("\texitCode: ").append(exitCode).append(";");
            }
        } else {
            // 无异常
            log.append("# 构建操作执行完成 ").append(action.getActionName());
        }
        log.append(" used: ")
                .append(action.getEndTime().getTime() - action.getStartTime().getTime())
                .append("ms\n");
        // 拼接日志
        this.appendLog(log.toString());
        // 拼接异常
        if (ex != null) {
            ex.printStackTrace(new PrintStream(appender));
        }
    }

    /**
     * 拼接停止日志
     */
    private void appendTerminatedLog() {
        StringBuilder log = new StringBuilder()
                .append("\n# 构建操作手动停止 ").append(action.getActionName())
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
        ApplicationBuildActionDO update = new ApplicationBuildActionDO();
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
        applicationBuildService.updateActionById(update);
    }

    @Override
    public void close() {
        // 关闭日志
        Streams.close(appender);
    }

}
