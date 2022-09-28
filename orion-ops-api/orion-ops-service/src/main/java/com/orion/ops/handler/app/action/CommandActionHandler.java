package com.orion.ops.handler.app.action;

import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.io.Streams;
import com.orion.net.remote.CommandExecutors;
import com.orion.net.remote.ExitCode;
import com.orion.net.remote.channel.ssh.CommandExecutor;
import com.orion.ops.constant.common.StainCode;
import com.orion.ops.utils.Utils;
import lombok.Getter;

/**
 * 执行操作-机器命令
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.constant.app.ActionType#BUILD_COMMAND
 * @see com.orion.ops.constant.app.ActionType#RELEASE_COMMAND
 * @since 2022/2/11 15:59
 */
public class CommandActionHandler extends AbstractActionHandler {

    private CommandExecutor executor;

    @Getter
    private Integer exitCode;

    public CommandActionHandler(Long actionId, MachineActionStore store) {
        super(actionId, store);
    }

    @Override
    protected void handler() throws Exception {
        this.appendLog(Utils.getStainKeyWords("# 开始执行\n", StainCode.GLOSS_BLUE));
        // 打开executor
        this.executor = store.getSessionStore().getCommandExecutor(Strings.replaceCRLF(action.getActionCommand()));
        // 执行命令
        CommandExecutors.syncExecCommand(executor, appender);
        this.exitCode = executor.getExitCode();
        if (!ExitCode.isSuccess(exitCode)) {
            throw Exceptions.execute("*** 命令执行失败 exitCode: " + exitCode);
        }
    }

    @Override
    public void terminate() {
        super.terminate();
        // 关闭executor
        Streams.close(executor);
    }

    @Override
    public void write(String command) {
        executor.write(command);
    }

    @Override
    public void close() {
        super.close();
        // 关闭executor
        Streams.close(executor);
    }

}
