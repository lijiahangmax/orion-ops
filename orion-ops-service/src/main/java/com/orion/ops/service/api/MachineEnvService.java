package com.orion.ops.service.api;

import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.domain.MachineEnvDO;
import com.orion.ops.entity.request.MachineEnvRequest;
import com.orion.ops.entity.vo.MachineEnvVO;

import java.util.List;

/**
 * 环境变量服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 11:43
 */
public interface MachineEnvService {

    /**
     * 添加 变量
     *
     * @param request request
     * @return id
     */
    Long addEnv(MachineEnvRequest request);

    /**
     * 修改 变量
     *
     * @param request request
     * @return effect
     */
    Integer updateEnv(MachineEnvRequest request);

    /**
     * 通过id删除
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteEnv(List<Long> idList);

    /**
     * 合并属性
     *
     * @param sourceMachineId 原始机器id
     * @param targetMachineId 目标机器id
     * @return effect
     */
    Integer mergeEnv(Long sourceMachineId, Long targetMachineId);

    /**
     * 列表
     *
     * @param request request
     * @return effect
     */
    DataGrid<MachineEnvVO> listEnv(MachineEnvRequest request);

    /**
     * 获取机器变量
     *
     * @param machineId machineId
     * @param env       envKey
     * @return env
     */
    String getMachineEnv(Long machineId, String env);

    /**
     * 获取机器环境变量
     *
     * @param machineId machineId
     * @return map
     */
    MutableLinkedHashMap<String, String> getMachineEnv(Long machineId);

    /**
     * 初始化机器环境
     *
     * @param machineId 机器id
     */
    void initEnv(Long machineId);

}
