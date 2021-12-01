package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * app 版本控制请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 18:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationVcsRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * url
     */
    private String url;

    /**
     * name
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 类型 1git
     *
     * @see com.orion.ops.consts.app.VcsType
     */
    private Integer type;

    /**
     * 状态 10未初始化 20初始化中 30正常 40失败
     *
     * @see com.orion.ops.consts.app.VcsStatus
     */
    private Integer status;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * token
     */
    private String token;

    /**
     * 分支名称
     */
    private String branchName;

}
