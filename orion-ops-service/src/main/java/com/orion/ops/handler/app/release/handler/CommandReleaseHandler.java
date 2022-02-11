package com.orion.ops.handler.app.release.handler;

import com.orion.ops.handler.app.store.ReleaseStore;
import com.orion.ops.handler.app.store.MachineStore;
import com.orion.remote.ExitCode;
import com.orion.remote.channel.ssh.CommandExecutor;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.io.Streams;
import lombok.Getter;

/**
 * 发布命令处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/28 18:07
 */
public class CommandReleaseHandler extends AbstractReleaseHandler {

    private CommandExecutor executor;

    @Getter
    private Integer exitCode;

    public CommandReleaseHandler(Long actionId, ReleaseStore store, MachineStore machineStore) {
        super(actionId, store, machineStore);
    }

    @Override
    protected void handler() throws Exception {
        // 打开executor
        this.executor = machineStore.getSessionStore().getCommandExecutor(Strings.replaceCRLF(action.getActionCommand()));
        executor.inherit()
                .sync()
                .transfer(appender)
                .connect()
                .exec();
        this.exitCode = executor.getExitCode();
        if (!ExitCode.SUCCESS.getCode().equals(exitCode)) {
            throw Exceptions.log("*** 命令执行失败 exitCode: " + exitCode);
        }
    }

    @Override
    public void terminated() {
        super.terminated();
        // 关闭executor
        Streams.close(executor);
    }

    @Override
    public void close() {
        super.close();
        // 关闭executor
        Streams.close(executor);
    }

}
