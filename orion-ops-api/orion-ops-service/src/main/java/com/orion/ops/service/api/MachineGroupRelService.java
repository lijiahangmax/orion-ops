package com.orion.ops.service.api;

import java.util.List;

/**
 * 机器分组关联服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/23 16:10
 */
public interface MachineGroupRelService {

    /**
     * 组内添加机器 一组多机器
     *
     * @param groupId       groupId
     * @param machineIdList machineIdList
     */
    void addMachineRelByGroup(Long groupId, List<Long> machineIdList);

    /**
     * 组内添加机器 一机器多组
     *
     * @param machineId   machineId
     * @param groupIdList groupIdList
     */
    void addMachineRelByMachineId(Long machineId, List<Long> groupIdList);

    /**
     * 组内删除机器
     *
     * @param id id
     * @return effect
     */
    Integer deleteById(Long id);

    /**
     * 通过机器id删除
     *
     * @param machineIdList machineIdList
     * @return effect
     */
    Integer deleteByMachineIdList(List<Long> machineIdList);

    /**
     * 通过分组id删除
     *
     * @param groupIdList groupIdList
     * @return effect
     */
    Integer deleteByGroupIdList(List<Long> groupIdList);

    //   机器查询所有list并且查询rel 移动 添加/移动去重

}
