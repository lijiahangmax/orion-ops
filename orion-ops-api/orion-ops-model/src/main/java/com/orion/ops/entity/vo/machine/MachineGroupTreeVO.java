package com.orion.ops.entity.vo.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 机器分组树响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/23 17:30
 */
@Data
@ApiModel(value = "机器分组树响应")
public class MachineGroupTreeVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "父id")
    private Long parentId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "子节点")
    private List<MachineGroupTreeVO> children;

}
