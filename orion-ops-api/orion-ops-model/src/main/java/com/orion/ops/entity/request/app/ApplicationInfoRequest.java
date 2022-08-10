package com.orion.ops.entity.request.app;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 18:57
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用请求")
@SuppressWarnings("ALL")
public class ApplicationInfoRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "唯一标识")
    private String tag;

    @ApiModelProperty(value = "版本仓库id")
    private Long repoId;

    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * @see com.orion.ops.constant.Const#INCREMENT
     * @see com.orion.ops.constant.Const#DECREMENT
     */
    @ApiModelProperty(value = "排序调整方向")
    private Integer sortAdjust;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     */
    @ApiModelProperty(value = "是否查询机器")
    private Integer queryMachine;

}
