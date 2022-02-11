package com.orion.ops.handler.app.release.machine;

import com.orion.able.SafeCloseable;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.handler.app.store.MachineStore;
import com.orion.ops.handler.app.store.ReleaseStore;

/**
 * 机器处理器接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/29 8:52
 */
public interface IMachineProcessor extends Runnable, SafeCloseable {

    /**
     * 获取状态
     *
     * @return status
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
     * 获取发布机器处理器
     *
     * @param releaseStore releaseStore
     * @param machineStore machineStore
     * @return 发布机器处理器
     */
    static IMachineProcessor with(ReleaseStore releaseStore, MachineStore machineStore) {
        return new MachineProcessor(releaseStore, machineStore);
    }

}
