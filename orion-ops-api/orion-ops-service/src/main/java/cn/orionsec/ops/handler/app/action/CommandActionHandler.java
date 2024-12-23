/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
package cn.orionsec.ops.handler.app.action;

import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.io.Streams;
import cn.orionsec.kit.net.host.ssh.ExitCode;
import cn.orionsec.kit.net.host.ssh.command.CommandExecutor;
import cn.orionsec.kit.net.host.ssh.command.CommandExecutors;
import cn.orionsec.ops.constant.app.ActionType;
import cn.orionsec.ops.constant.common.StainCode;
import cn.orionsec.ops.utils.Utils;
import lombok.Getter;

/**
 * 执行操作-机器命令
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see ActionType#BUILD_COMMAND
 * @see ActionType#RELEASE_COMMAND
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
        CommandExecutors.execCommand(executor, appender);
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
