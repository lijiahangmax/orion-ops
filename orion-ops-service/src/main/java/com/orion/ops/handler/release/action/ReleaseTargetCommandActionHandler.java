package com.orion.ops.handler.release.action;

import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.handler.release.hint.ReleaseActionHint;
import com.orion.ops.handler.release.hint.ReleaseHint;
import com.orion.ops.handler.release.hint.ReleaseMachineHint;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.ssh.CommandExecutor;
import com.orion.support.Attempt;
import com.orion.utils.Exceptions;
import com.orion.utils.io.Streams;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * 目标机器命令执行处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.consts.app.ActionType#TARGET_COMMAND
 * @since 2021/7/25 0:37
 */
@Slf4j
public class ReleaseTargetCommandActionHandler extends AbstractReleaseActionHandler {

    private ReleaseMachineHint machineHint;

    private CommandExecutor executor;

    public ReleaseTargetCommandActionHandler(ReleaseHint hint, ReleaseMachineHint machineHint, ReleaseActionHint action) {
        super(hint, action);
        this.machineHint = machineHint;
    }

    @Override
    public void handle() throws Exception {
        this.setLoggerAppender();
        super.handled = true;
        super.startTime = new Date();
        super.updateActionStatus(action.getId(), ActionStatus.RUNNABLE, startTime, null);
        Exception e = null;
        try {
            // 执行操作
            this.handleAction();
        } catch (Exception ex) {
            e = ex;
        }
        // 完成回调
        this.endTime = new Date();
        if (e == null) {
            super.updateActionStatus(action.getId(), ActionStatus.FINISH, null, endTime);
        } else {
            super.success = false;
            log.error("上线单处理目标机器操作-处理操作 异常: {}", e.getMessage());
            super.updateActionStatus(action.getId(), ActionStatus.EXCEPTION, null, endTime);
            super.onException(e);
        }
        Streams.close(this);
    }

    @Override
    protected void handleAction() throws IOException {
        this.appendLog("开始执行远程机器命令: {}", action.getCommand());
        try {
            SessionStore session = hint.getSessionHolder().get(machineHint.getMachineId());
            if (!session.isConnected()) {
                session.connect();
            }
            this.executor = session.getCommandExecutor(action.getCommand());
            executor.sync()
                    .inherit()
                    .streamHandler(Attempt.rethrows(this::handlerStandardOutputStream))
                    .connect()
                    .exec();
        } catch (Exception e) {
            this.appendLog("执行远程机器命令失败: {}, err: {} {}", action.getCommand(), e.getClass().getName(), e.getMessage());
            throw e;
        }
        int exitCode = executor.getExitCode();
        if (exitCode != 0) {
            throw Exceptions.log("执行远程机器命令正常结束 exitCode: " + exitCode);
        } else {
            this.appendLog("执行远程机器命令成功");
        }
    }

    @Override
    protected void setLoggerAppender() {
        super.setLoggerAppender();
        appender.then(machineHint.getLogOutputStream()).onClose(false);
    }

    /**
     * 处理标准输出流
     *
     * @param inputStream inputStream
     * @throws IOException IOException
     */
    private void handlerStandardOutputStream(InputStream inputStream) throws IOException {
        Streams.transfer(inputStream, appender);
    }

    @Override
    public void close() {
        super.close();
        Streams.close(executor);
    }

}
