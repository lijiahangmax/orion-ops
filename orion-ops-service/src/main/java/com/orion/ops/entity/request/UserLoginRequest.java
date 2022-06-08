package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户登录请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/20 23:01
 */
@Data
@ApiModel(value = "用户登录请求")
public class UserLoginRequest {

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "ip")
    private String ip;

}
