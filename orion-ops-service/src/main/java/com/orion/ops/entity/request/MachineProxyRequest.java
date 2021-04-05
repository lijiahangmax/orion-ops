package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 添加/修改 代理请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:38
 */
@Data
public class MachineProxyRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 描述
     */
    private String description;

}
