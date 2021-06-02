package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
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
     * 添加/修改 变量
     *
     * @param request request
     * @return id/effect
     */
    Long addUpdateEnv(MachineEnvRequest request);

    /**
     * 通过id删除
     *
     * @param ids ids
     * @return effect
     */
    Integer deleteEnv(List<Long> ids);

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
     * 初始化机器环境
     *
     * @param machineId 机器id
     */
    void initEnv(Long machineId);

}
