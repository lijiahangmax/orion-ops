package com.orion.ops.entity.vo.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 机器秘钥响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/5 13:44
 */
@Data
@ApiModel(value = "机器秘钥响应")
@SuppressWarnings("ALL")
public class MachineSecretKeyVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "秘钥名称")
    private String name;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * @see com.orion.ops.constant.machine.MountKeyStatus
     */
    @ApiModelProperty(value = "挂载状态 1未找到 2已挂载 3未挂载")
    private Integer mountStatus;

}
