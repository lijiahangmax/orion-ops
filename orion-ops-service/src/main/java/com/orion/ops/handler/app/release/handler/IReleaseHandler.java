package com.orion.ops.handler.app.release.handler;

import com.orion.able.Executable;
import com.orion.able.SafeCloseable;
import com.orion.function.select.Branches;
import com.orion.function.select.Selector;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.entity.domain.ApplicationReleaseActionDO;
import com.orion.ops.handler.app.store.ReleaseStore;
import com.orion.ops.handler.app.store.MachineStore;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 发布操作处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/28 17:59
 */
public interface IReleaseHandler extends Executable, SafeCloseable {

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
    Integer getExitCode();

    /**
     * 获取发布操作处理器
     *
     * @param actions      actions
     * @param store        store
     * @param machineStore machineStore
     * @return list
     */
    static List<IReleaseHandler> with(List<ApplicationReleaseActionDO> actions, ReleaseStore store, MachineStore machineStore) {
        return actions.stream()
                .map(action -> Selector.<ActionType, IReleaseHandler>of(ActionType.of(action.getActionType()))
                        .test(Branches.eq(ActionType.RELEASE_TRANSFER)
                                .then(() -> new TransferReleaseHandler(action.getId(), store, machineStore)))
                        .test(Branches.eq(ActionType.RELEASE_COMMAND)
                                .then(() -> new CommandReleaseHandler(action.getId(), store, machineStore)))
                        .get())
                .collect(Collectors.toList());
    }

}
