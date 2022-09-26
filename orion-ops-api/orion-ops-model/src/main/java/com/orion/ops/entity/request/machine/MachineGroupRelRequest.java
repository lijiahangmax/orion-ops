package com.orion.ops.entity.request.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 机器分组请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/23 16:22
 */
@Data
@ApiModel(value = "机器分组请求")
public class MachineGroupRelRequest {

    @ApiModelProperty(value = "组id")
    private Long groupId;

    @ApiModelProperty(value = "目标组id")
    private Long targetGroupId;

    @ApiModelProperty(value = "机器id")
    private List<Long> machineIdList;

}
