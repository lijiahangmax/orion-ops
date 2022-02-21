package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 配置 ip 列表
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 22:13
 */
@Data
public class ConfigIpListRequest {

    /**
     * ip 白名单
     */
    private String whiteIpList;

    /**
     * ip 黑名单
     */
    private String blackIpList;

    /**
     * 是否启用 ip 过滤
     */
    private Boolean enableIpFilter;

    /**
     * 是否启用 ip 白名单
     */
    private Boolean enableWhiteIpList;

}
