/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orion.ops.service.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orion.ops.entity.request.machine.MachineMonitorEndpointRequest;

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

    /**
     * 查询磁盘名称
     *
     * @param machineId machineId
     * @return 名称
     */
    JSONArray getDiskName(Long machineId);

    /**
     * 获取cpu图标
     *
     * @param request request
     * @return 图表
     */
    JSONObject getCpuChart(MachineMonitorEndpointRequest request);

    /**
     * 获取内存图标
     *
     * @param request request
     * @return 图表
     */
    JSONObject getMemoryChart(MachineMonitorEndpointRequest request);

    /**
     * 获取网络图标
     *
     * @param request request
     * @return 图表
     */
    JSONObject getNetChart(MachineMonitorEndpointRequest request);

    /**
     * 获取磁盘图标
     *
     * @param request request
     * @return 图表
     */
    JSONObject getDiskChart(MachineMonitorEndpointRequest request);
}
