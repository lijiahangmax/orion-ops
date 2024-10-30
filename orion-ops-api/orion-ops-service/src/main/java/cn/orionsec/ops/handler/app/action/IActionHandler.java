/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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

import cn.orionsec.ops.constant.app.ActionStatus;
import cn.orionsec.ops.constant.app.ActionType;
import cn.orionsec.ops.constant.app.TransferMode;
import cn.orionsec.ops.entity.domain.ApplicationActionLogDO;
import com.orion.lang.able.Executable;
import com.orion.lang.able.SafeCloseable;
import com.orion.lang.function.select.Branches;
import com.orion.lang.function.select.Selector;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 构建处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/11 15:29
 */
public interface IActionHandler extends Executable, SafeCloseable {

    /**
     * 获取状态
     *
     * @return status
     * @see ActionStatus
     */
    ActionStatus getStatus();

    /**
     * 跳过
     */
    void skip();

    /**
     * 终止
     */
    void terminate();

    /**
     * 输入命令
     *
     * @param command command
     */
    default void write(String command) {
    }

    /**
     * 获取退出码
     *
     * @return exitCode
     */
    default Integer getExitCode() {
        return null;
    }

    /**
     * 创建处理器
     *
     * @param actions actions
     * @param store   store
     * @return handler
     */
    static List<IActionHandler> createHandler(List<ApplicationActionLogDO> actions, MachineActionStore store) {
        return actions.stream()
                .map(action -> Selector.<ActionType, IActionHandler>of(ActionType.of(action.getActionType()))
                        .test(Branches.eq(ActionType.BUILD_CHECKOUT)
                                .then(() -> new CheckoutActionHandler(action.getId(), store)))
                        .test(Branches.in(ActionType.BUILD_COMMAND, ActionType.RELEASE_COMMAND)
                                .then(() -> new CommandActionHandler(action.getId(), store)))
                        .test(Branches.eq(ActionType.RELEASE_TRANSFER)
                                .then(() -> {
                                    if (TransferMode.SCP.getValue().equals(store.getTransferMode())) {
                                        return new ScpTransferActionHandler(action.getId(), store);
                                    } else {
                                        return new SftpTransferActionHandler(action.getId(), store);
                                    }
                                }))
                        .get())
                .collect(Collectors.toList());
    }

}
