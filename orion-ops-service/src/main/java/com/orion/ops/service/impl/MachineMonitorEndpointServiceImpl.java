package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.ParamConst;
import com.orion.ops.handler.http.HttpApiRequest;
import com.orion.ops.handler.http.HttpApiRequester;
import com.orion.ops.handler.http.MachineMonitorHttpApi;
import com.orion.ops.handler.http.MachineMonitorHttpApiRequester;
import com.orion.ops.service.api.MachineMonitorEndpointService;
import com.orion.ops.utils.Valid;
import org.springframework.data.redis.core.RedisTemplate;
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
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Integer ping(Long machineId) {
        HttpWrapper<Integer> wrapper = this.getRequester(machineId, MachineMonitorHttpApi.ENDPOINT_PING)
                .request(Integer.class);
        return Valid.api(wrapper);
    }

    @Override
    public void setMachineId(Long machineId) {
        HttpApiRequest request = this.getRequester(machineId, MachineMonitorHttpApi.ENDPOINT_SET_MACHINE_ID)
                .getRequest();
        request.queryParam(ParamConst.MACHINE_ID, machineId.toString());
        Valid.validHttpOk(request.await());
    }

    @Override
    public void startCollect(Long machineId) {
        HttpApiRequest request = this.getRequester(machineId, MachineMonitorHttpApi.ENDPOINT_START)
                .getRequest();
        Valid.validHttpOk(request.await());
    }

    @Override
    public void stopCollect(Long machineId) {
        HttpApiRequest request = this.getRequester(machineId, MachineMonitorHttpApi.ENDPOINT_STOP)
                .getRequest();
        Valid.validHttpOk(request.await());
    }

    @Override
    public JSONObject getBaseMetrics(Long machineId) {
        HttpApiRequest request = this.getRequester(machineId, MachineMonitorHttpApi.METRICS_BASE)
                .getRequest();
        request.queryParam(ParamConst.LIMIT, Const.N_10.toString());
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
        request.queryParam(ParamConst.LIMIT, Const.N_10.toString())
                .queryParam(ParamConst.NAME, name);
        HttpWrapper<JSONArray> wrapper = request.getHttpWrapper(JSONArray.class);
        return Valid.api(wrapper);
    }

    /**
     * 获取 agent api 请求
     *
     * @param machineId machineId
     * @param api       api
     * @return request
     */
    private HttpApiRequester<?> getRequester(Long machineId, MachineMonitorHttpApi api) {
        return MachineMonitorHttpApiRequester.builder()
                .url("http://101.43.254.243:9220")
                .api(api)
                .accessToken("agent_access")
                .build();
    }

}
