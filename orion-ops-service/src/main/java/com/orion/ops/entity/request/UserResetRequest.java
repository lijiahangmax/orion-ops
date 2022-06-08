package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 重置密码请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/21 23:13
 */
@Data
@ApiModel(value = "重置密码请求")
public class UserResetRequest {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "原密码")
    private String beforePassword;

    @ApiModelProperty(value = "密码")
    private String password;

}
