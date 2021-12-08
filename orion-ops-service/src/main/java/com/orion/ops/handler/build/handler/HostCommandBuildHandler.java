package com.orion.ops.handler.build.handler;

import com.orion.ops.handler.build.BuildStore;
import com.orion.remote.channel.ssh.CommandExecutor;
import com.orion.utils.io.Streams;
import lombok.Getter;

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

    @Getter
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
    }

    @Override
    public void close() {
        super.close();
        // 关闭executor
        Streams.close(executor);
    }

}
