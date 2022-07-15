package com.orion.ops.entity.request;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 批量执行请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 17:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "批量执行请求")
public class CommandExecRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "执行机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "执行机器id")
    private List<Long> machineIdList;

    @ApiModelProperty(value = "执行主机")
    private String host;

    @ApiModelProperty(value = "命令")
    private String command;

    @ApiModelProperty(value = "执行人")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * @see com.orion.ops.consts.command.ExecStatus
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * @see com.orion.ops.consts.command.ExecType
     */
    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "退出码")
    private Integer exitCode;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "是否省略命令")
    private boolean omitCommand;

}
