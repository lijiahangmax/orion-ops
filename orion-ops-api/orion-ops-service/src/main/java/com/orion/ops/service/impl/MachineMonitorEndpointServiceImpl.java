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
package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.ParamConst;
import com.orion.ops.entity.domain.MachineMonitorDO;
import com.orion.ops.entity.request.machine.MachineMonitorEndpointRequest;
import com.orion.ops.handler.http.HttpApiRequest;
import com.orion.ops.handler.http.HttpApiRequester;
import com.orion.ops.handler.http.MachineMonitorHttpApi;
import com.orion.ops.handler.http.MachineMonitorHttpApiRequester;
import com.orion.ops.service.api.MachineMonitorEndpointService;
import com.orion.ops.service.api.MachineMonitorService;
import com.orion.ops.utils.Valid;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 机器监控端点服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 15:02
 */
@Service("machineMonitorEndpointService")
public class MachineMonitorEndpointServiceImpl implements MachineMonitorEndpointService {

    @Resource
    private MachineMonitorService machineMonitorService;

    @Override
    public Integer ping(Long machineId) {
        HttpWrapper<Integer> wrapper = this.getRequester(machineId, MachineMonitorHttpApi.ENDPOINT_PING)
                .request(Integer.class);
        return Valid.api(wrapper);
    }

    @Override
    public JSONObject getBaseMetrics(Long machineId) {
        HttpApiRequest request = this.getRequester(machineId, MachineMonitorHttpApi.METRICS_BASE)
                .getRequest();
        request.queryParam(ParamConst.LIMIT, Const.N_10);
        HttpWrapper<JSONObject> wrapper = request.getHttpWrapper(JSONObject.class);
        return Valid.api(wrapper);
    }

    @Override
    public JSONObject getSystemLoad(Long machineId) {
        HttpWrapper<JSONObject> wrapper = this.getRequester(machineId, MachineMonitorHttpApi.METRICS_SYSTEM_LOAD)
                .request(JSONObject.class);
        return Valid.api(wrapper);
    }

    @Override
    public JSONArray getTopProcesses(Long machineId, String name) {
        HttpApiRequest request = this.getRequester(machineId, MachineMonitorHttpApi.METRICS_TOP_PROCESSES)
                .getRequest();
        request.queryParam(ParamConst.LIMIT, Const.N_10)
                .queryParam(ParamConst.NAME, name);
        HttpWrapper<JSONArray> wrapper = request.getHttpWrapper(JSONArray.class);
        return Valid.api(wrapper);
    }

    @Override
    public JSONArray getDiskName(Long machineId) {
        HttpWrapper<JSONArray> res = this.getRequester(machineId, MachineMonitorHttpApi.METRICS_DISK_NAME)
                .getRequest()
                .getHttpWrapper(JSONArray.class);
        return Valid.api(res);
    }

    @Override
    public JSONObject getCpuChart(MachineMonitorEndpointRequest request) {
        return getStatisticsChart(request, MachineMonitorHttpApi.MONITOR_CPU);
    }

    @Override
    public JSONObject getMemoryChart(MachineMonitorEndpointRequest request) {
        return getStatisticsChart(request, MachineMonitorHttpApi.MONITOR_MEMORY);
    }

    @Override
    public JSONObject getNetChart(MachineMonitorEndpointRequest request) {
        return getStatisticsChart(request, MachineMonitorHttpApi.MONITOR_NET);
    }

    @Override
    public JSONObject getDiskChart(MachineMonitorEndpointRequest request) {
        return getStatisticsChart(request, MachineMonitorHttpApi.MONITOR_DISK);
    }

    private JSONObject getStatisticsChart(MachineMonitorEndpointRequest request, MachineMonitorHttpApi api) {
        HttpWrapper<JSONObject> res = this.getRequester(request.getMachineId(), api)
                .getRequest()
                .jsonBody(request)
                .getHttpWrapper(JSONObject.class);
        return Valid.api(res);
    }

    /**
     * 获取 agent api 请求
     *
     * @param machineId machineId
     * @param api       api
     * @return request
     */
    private HttpApiRequester<?> getRequester(Long machineId, MachineMonitorHttpApi api) {
        MachineMonitorDO monitor = machineMonitorService.selectByMachineId(machineId);
        Valid.notNull(monitor, MessageConst.CONFIG_ABSENT);
        return MachineMonitorHttpApiRequester.builder()
                .url(monitor.getMonitorUrl())
                .accessToken(monitor.getAccessToken())
                .api(api)
                .build();
    }

}
