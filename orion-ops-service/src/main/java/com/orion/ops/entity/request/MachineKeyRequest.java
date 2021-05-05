package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机器秘钥请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/5 12:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MachineKeyRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 密码
     */
    private String password;

    /**
     * 描述
     */
    private String description;

    /**
     * 文件base64
     */
    private String file;

}
