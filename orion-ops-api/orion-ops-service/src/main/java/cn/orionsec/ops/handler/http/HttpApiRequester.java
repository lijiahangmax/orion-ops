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

import com.alibaba.fastjson.TypeReference;
import com.orion.http.ok.OkResponse;
import com.orion.lang.define.wrapper.HttpWrapper;

/**
 * http api 请求器基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 10:55
 */
public interface HttpApiRequester<API extends HttpApiDefined> {

    /**
     * 请求 api
     *
     * @return OkResponse
     */
    default OkResponse await() {
        return this.getRequest().await();
    }

    /**
     * 请求 api
     *
     * @param dataClass dataClass
     * @param <T>       T
     * @return HttpWrapper
     */
    default <T> HttpWrapper<T> request(Class<T> dataClass) {
        return this.getRequest().getHttpWrapper(dataClass);
    }

    /**
     * 请求 api
     *
     * @param type type
     * @param <T>  T
     * @return T
     */
    default <T> T request(TypeReference<T> type) {
        return this.getRequest().getJson(type);
    }

    /**
     * 请求 api
     *
     * @param requestBody requestBody
     * @param dataClass   dataClass
     * @param <T>         T
     * @return HttpWrapper
     */
    default <T> HttpWrapper<T> request(Object requestBody, Class<T> dataClass) {
        return this.getRequest().jsonBody(requestBody).getHttpWrapper(dataClass);
    }

    /**
     * 请求 api
     *
     * @param requestBody requestBody
     * @param type        type
     * @param <T>         T
     * @return T
     */
    default <T> T request(Object requestBody, TypeReference<T> type) {
        return this.getRequest().jsonBody(requestBody).getJson(type);
    }

    /**
     * 获取 api request
     *
     * @return request
     */
    HttpApiRequest getRequest();

}
