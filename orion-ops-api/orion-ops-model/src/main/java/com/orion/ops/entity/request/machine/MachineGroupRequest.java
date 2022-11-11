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
@SuppressWarnings("ALL")
public class MachineGroupRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "移动到的节点id")
    private Long targetId;

    @ApiModelProperty(value = "父id")
    private Long parentId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    /**
     * @see com.orion.ops.constant.common.TreeMoveType
     */
    @ApiModelProperty(value = "移动类型")
    private Integer moveType;

}
