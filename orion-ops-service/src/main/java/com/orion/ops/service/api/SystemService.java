package com.orion.ops.service.api;

import com.orion.ops.entity.request.ConfigIpListRequest;
import com.orion.ops.entity.vo.IpListConfigVO;

/**
 * 系统服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 22:15
 */
public interface SystemService {

    /**
     * 获取 ip 配置
     *
     * @param ip ip
     * @return ip 信息
     */
    IpListConfigVO getIpInfo(String ip);

    /**
     * 配置 ip 列表
     *
     * @param request request
     */
    void configIpList(ConfigIpListRequest request);

}
