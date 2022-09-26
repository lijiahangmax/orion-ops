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
public class MachineGroupRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "同级上个节点id")
    private Long prevId;

    @ApiModelProperty(value = "父id")
    private Long parentId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "排序")
    private Integer sort;

}
