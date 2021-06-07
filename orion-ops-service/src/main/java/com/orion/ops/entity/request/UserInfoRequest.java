package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/25 18:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserInfoRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * id集合
     */
    private List<Long> idList;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 密码
     */
    private String password;

    /**
     * 角色类型 10管理员 20开发 30运维
     *
     * @see com.orion.ops.consts.RoleType
     */
    private Integer role;

    /**
     * 用户状态 1启用 2禁用
     */
    private Integer status;

    /**
     * 联系手机
     */
    private String phone;

    /**
     * 联系邮箱
     */
    private String email;

    /**
     * 头像base64
     */
    private String headPic;

}
