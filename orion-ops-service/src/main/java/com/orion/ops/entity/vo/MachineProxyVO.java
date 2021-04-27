package com.orion.ops.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * 代理
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 22:01
 */
@Data
public class MachineProxyVO {

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
     * 代理类型
     */
    private Integer type;

    /**
     * 用户名
     */
    private String username;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

}
