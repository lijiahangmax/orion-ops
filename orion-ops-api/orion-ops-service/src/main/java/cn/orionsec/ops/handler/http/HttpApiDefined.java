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

import cn.orionsec.kit.http.support.HttpMethod;

/**
 * http api 定义接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 10:56
 */
public interface HttpApiDefined {

    /**
     * 请求路径
     *
     * @return 路径
     */
    String getPath();

    /**
     * 请求方法
     *
     * @return 方法
     */
    HttpMethod getMethod();

}
