package com.orion.ops.handler.http;

import com.orion.ops.constant.monitor.MonitorConst;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 机器监控插件 api 请求器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 11:03
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineMonitorHttpApiRequester implements HttpApiRequester<MachineMonitorHttpApi> {

    private static final String TAG = "machine-monitor";

    private String url;

    private MachineMonitorHttpApi api;

    private String accessToken;

    @Override
    public HttpApiRequest getRequest() {
        HttpApiRequest request = new HttpApiRequest(url, api);
        request.tag(TAG);
        request.header(MonitorConst.DEFAULT_ACCESS_HEADER, accessToken);
        return request;
    }

}
