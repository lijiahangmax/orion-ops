package com.orion.ops.handler.app.action;

import com.orion.able.Executable;
import com.orion.able.SafeCloseable;
import com.orion.function.select.Branches;
import com.orion.function.select.Selector;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.entity.domain.ApplicationActionLogDO;

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
    void skipped();

    /**
     * 终止
     */
    void terminated();

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
                                .then(() -> new TransferActionHandler(action.getId(), store)))
                        .get())
                .collect(Collectors.toList());
    }

}
