package com.orion.ops.handler.release.action;

import com.orion.ops.consts.Const;
import com.orion.ops.handler.release.hint.ReleaseActionHint;
import com.orion.ops.handler.release.hint.ReleaseHint;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.ssh.CommandExecutor;
import com.orion.support.Attempt;
import com.orion.utils.Exceptions;
import com.orion.utils.io.Streams;

import java.io.IOException;
import java.io.InputStream;

/**
 * 宿主机命令执行处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.consts.app.ActionType#HOST_COMMAND
 * @since 2021/7/15 17:19
 */
public class ReleaseCommandActionHandler extends AbstractReleaseActionHandler {

    private CommandExecutor executor;

    public ReleaseCommandActionHandler(ReleaseHint hint, ReleaseActionHint action) {
        super(hint, action);
    }

    @Override
    protected void handleAction() throws IOException {
        this.appendLog("开始执行命令: {}", action.getCommand());
        try {
            SessionStore session = hint.getSessionHolder().get(Const.HOST_MACHINE_ID);
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
            this.appendLog("命令执行失败: {}, err: {} {}", action.getCommand(), e.getClass().getName(), e.getMessage());
            throw e;
        }
        int exitCode = executor.getExitCode();
        if (exitCode != 0) {
            throw Exceptions.log("执行命令非正常结束 exitCode: " + exitCode);
        } else {
            this.appendLog("执行命令成功");
        }
    }

    @Override
    protected void setLoggerAppender() {
        super.setLoggerAppender();
        appender.then(hint.getHostLogOutputStream()).onClose(false);
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
