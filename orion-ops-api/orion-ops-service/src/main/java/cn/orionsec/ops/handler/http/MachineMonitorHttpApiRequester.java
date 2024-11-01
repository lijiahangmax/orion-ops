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
package cn.orionsec.ops.handler.http;

import cn.orionsec.ops.constant.monitor.MonitorConst;
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
