package com.orion.ops.handler.build.handler;

import com.orion.ops.handler.build.BuildStore;
import com.orion.remote.ExitCode;
import com.orion.remote.channel.ssh.CommandExecutor;
import com.orion.utils.Exceptions;
import com.orion.utils.io.Streams;

/**
 * 构建执行操作-主机命令
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.consts.app.ActionType#BUILD_HOST_COMMAND
 * @since 2021/12/6 10:54
 */
public class HostCommandBuildHandler extends AbstractBuildHandler {

    private CommandExecutor executor;

    private Integer exitCode;

    public HostCommandBuildHandler(Long actionId, BuildStore store) {
        super(actionId, store);
    }

    @Override
    protected void handler() throws Exception {
        // 打开handler
        this.executor = store.getSessionStore().getCommandExecutor(action.getActionCommand());
        executor.inherit()
                .sync()
                .transfer(appender)
                .connect()
                .exec();
        this.exitCode = executor.getExitCode();
        if (!ExitCode.SUCCESS.getCode().equals(this.exitCode)) {
            throw Exceptions.log("*** 命令执行失败 exitCode: " + exitCode);
        }
    }

    @Override
    public void close() {
        super.close();
        // 关闭executor
        Streams.close(executor);
    }

    @Override
    public Integer getExitCode() {
        return exitCode;
    }

}
