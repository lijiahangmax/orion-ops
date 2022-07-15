package com.orion.ops.entity.request;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 机器秘钥请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/5 12:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "机器秘钥请求")
public class MachineKeyRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * @see com.orion.ops.consts.machine.MountKeyStatus
     */
    @ApiModelProperty(value = "挂载状态 1未找到 2已挂载 3未挂载")
    private Integer mountStatus;

    @ApiModelProperty(value = "文件base64")
    private String file;

}
