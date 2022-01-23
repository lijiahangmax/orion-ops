package com.orion.ops.service.api;

import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.domain.MachineEnvDO;
import com.orion.ops.entity.request.MachineEnvRequest;
import com.orion.ops.entity.vo.MachineEnvVO;

import java.util.List;
import java.util.Map;

/**
 * 环境变量服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 11:43
 */
public interface MachineEnvService {

    /**
     * 添加变量
     *
     * @param request request
     * @return id
     */
    Long addEnv(MachineEnvRequest request);

    /**
     * 修改变量
     *
     * @param request request
     * @return effect
     */
    Integer updateEnv(MachineEnvRequest request);

    /**
     * 修改变量
     *
     * @param before  before
     * @param request request
     * @return effect
     */
    Integer updateEnv(MachineEnvDO before, MachineEnvRequest request);

    /**
     * 通过id删除
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteEnv(List<Long> idList);

    /**
     * 批量添加
     *
     * @param machineId machineId
     * @param env       env
     */
    void saveEnv(Long machineId, Map<String, String> env);

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
     * @return rows
     */
    DataGrid<MachineEnvVO> listEnv(MachineEnvRequest request);

    /**
     * 详情
     *
     * @param id id
     * @return row
     */
    MachineEnvVO getEnvDetail(Long id);

    /**
     * 同步机器属性
     *
     * @param request request
     */
    void syncMachineEnv(MachineEnvRequest request);

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
     * 获取机器环境变量 包含机器信息
     *
     * @param machineId machineId
     * @return map
     */
    MutableLinkedHashMap<String, String> getFullMachineEnv(Long machineId);

    /**
     * 初始化机器环境
     *
     * @param machineId 机器id
     */
    void initEnv(Long machineId);

    /**
     * 删除机器环境变量
     *
     * @param machineId 机器id
     * @return effect
     */
    Integer deleteEnvByMachineId(Long machineId);

    /**
     * 获取sftp编码格式
     *
     * @param machineId 机器id
     * @return 编码格式
     */
    String getSftpCharset(Long machineId);

    /**
     * 获取文件tail模型
     *
     * @param machineId 机器id
     * @return mode
     */
    String getMachineTailMode(Long machineId);

    /**
     * 获取文件tail 尾行偏移量
     *
     * @param machineId 机器id
     * @return offset line
     */
    Integer getTailOffset(Long machineId);

    /**
     * 获取编码集
     *
     * @param machineId 机器id
     * @return 编码集
     */
    String getTailCharset(Long machineId);

}
