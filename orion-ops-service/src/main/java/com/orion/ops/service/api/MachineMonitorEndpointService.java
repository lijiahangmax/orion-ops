package com.orion.ops.service.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 机器监控端点服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 15:02
 */
public interface MachineMonitorEndpointService {

    /**
     * 发送 ping
     *
     * @param machineId machineId
     * @return pong:1
     */
    Integer ping(Long machineId);

    /**
     * 设置机器 id
     *
     * @param machineId machineId
     */
    void setMachineId(Long machineId);

    /**
     * 开始收集数据
     *
     * @param machineId machineId
     */
    void startCollect(Long machineId);

    /**
     * 停止收集数据
     *
     * @param machineId machineId
     */
    void stopCollect(Long machineId);

    /**
     * 获取基本指标信息
     *
     * @param machineId machineId
     * @return metrics
     */
    JSONObject getBaseMetrics(Long machineId);

    /**
     * 获取系统负载
     *
     * @param machineId machineId
     * @return load
     */
    JSONObject getSystemLoad(Long machineId);

    /**
     * 获取 top 进程
     *
     * @param machineId machineId
     * @param name      名称
     * @return 进程
     */
    JSONArray getTopProcesses(Long machineId, String name);

}
