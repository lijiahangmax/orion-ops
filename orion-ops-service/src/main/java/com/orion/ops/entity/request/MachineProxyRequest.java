package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 添加/修改 代理请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MachineProxyRequest extends PageRequest {

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
