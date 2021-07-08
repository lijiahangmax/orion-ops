package com.orion.ops.entity.request;

import lombok.Data;

/**
 * app 配置部署操作请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/7 19:00
 */
@Data
public class ApplicationConfigDeployActionRequest {

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     *
     * @see com.orion.ops.consts.app.ActionType
     */
    private Integer type;

    /**
     * 执行命令
     */
    private String command;

}
