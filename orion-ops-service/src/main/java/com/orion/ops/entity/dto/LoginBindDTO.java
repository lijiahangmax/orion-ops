package com.orion.ops.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/18 23:32
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LoginBindDTO implements Serializable {

    /**
     * 登陆时间戳
     */
    private Long timestamp;

    /**
     * 登陆 IP
     */
    private String loginIp;

}
