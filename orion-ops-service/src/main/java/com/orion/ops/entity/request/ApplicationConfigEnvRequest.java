package com.orion.ops.entity.request;

import lombok.Data;

/**
 * app配置环境请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 18:50
 */
@Data
public class ApplicationConfigEnvRequest {

    /**
     * 构建产物目录
     */
    private String bundlePath;

}
