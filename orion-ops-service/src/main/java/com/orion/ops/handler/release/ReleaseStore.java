package com.orion.ops.handler.release;

import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.ops.handler.release.machine.IMachineProcessor;
import com.orion.ops.handler.release.machine.MachineStore;
import com.orion.utils.collect.Maps;
import lombok.Data;

import java.util.Map;

/**
 * 发布存储对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/28 17:38
 */
@Data
public class ReleaseStore {

    private ApplicationReleaseDO record;

    /**
     * 发布机器
     */
    private Map<Long, MachineStore> machines;

    /**
     * 机器处理器
     */
    private Map<Long, IMachineProcessor> machineProcessors;

    public ReleaseStore() {
        this.machines = Maps.newLinkedMap();
        this.machineProcessors = Maps.newLinkedMap();
    }

}
