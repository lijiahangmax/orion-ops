package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * ip 列表配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 22:09
 */
@Data
public class IpListConfigVO {

    /**
     * 当前 ip
     */
    private String currentIp;

    /**
     * ip 位置
     */
    private String ipLocation;

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
