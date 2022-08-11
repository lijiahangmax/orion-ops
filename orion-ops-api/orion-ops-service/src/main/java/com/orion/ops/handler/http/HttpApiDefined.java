package com.orion.ops.handler.http;

import com.orion.http.support.HttpMethod;

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
