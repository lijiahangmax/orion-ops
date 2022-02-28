package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 系统配置请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/18 22:23
 */
@Data
public class SystemOptionRequest {

    /**
     * 配置项
     *
     * @see com.orion.ops.consts.system.SystemConfigKey
     */
    private Integer option;

    /**
     * 值
     */
    private String value;

}
