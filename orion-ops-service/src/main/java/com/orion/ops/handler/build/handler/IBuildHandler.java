package com.orion.ops.handler.build.handler;

import com.orion.able.Executable;
import com.orion.able.SafeCloseable;
import com.orion.function.select.Branches;
import com.orion.function.select.Selector;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.entity.domain.ApplicationBuildActionDO;
import com.orion.ops.handler.build.BuildStore;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 构建处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/6 8:50
 */
public interface IBuildHandler extends Executable, SafeCloseable {

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
     * 创建处理器
     *
     * @param actions actions
     * @param store   store
     * @return handler
     */
    static List<IBuildHandler> createHandler(List<ApplicationBuildActionDO> actions, BuildStore store) {
        return actions.stream()
                .map(action -> Selector.<ActionType, IBuildHandler>of(ActionType.of(action.getActionType()))
                        .test(Branches.eq(ActionType.BUILD_CHECKOUT)
                                .then(() -> new CheckoutBuildHandler(action.getId(), store)))
                        .test(Branches.eq(ActionType.BUILD_HOST_COMMAND)
                                .then(() -> new HostCommandBuildHandler(action.getId(), store)))
                        .get())
                .collect(Collectors.toList());
    }

}
