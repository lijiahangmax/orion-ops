package com.orion.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 登陆绑定信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/18 23:32
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "登陆绑定信息")
public class LoginBindDTO implements Serializable {

    @ApiModelProperty(value = "登陆时间戳")
    private Long timestamp;

    @ApiModelProperty(value = "登陆 IP")
    private String loginIp;

}
