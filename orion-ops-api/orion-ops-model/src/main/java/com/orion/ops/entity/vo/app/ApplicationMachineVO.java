package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用关联机器信息响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 19:07
 */
@Data
@ApiModel(value = "应用关联机器信息响应")
public class ApplicationMachineVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器主机")
    private String machineHost;

    @ApiModelProperty(value = "唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "当前版本发布id")
    private Long releaseId;

    @ApiModelProperty(value = "当前版本构建id")
    private Long buildId;

    @ApiModelProperty(value = "当前版本构建序列")
    private Integer buildSeq;

}
