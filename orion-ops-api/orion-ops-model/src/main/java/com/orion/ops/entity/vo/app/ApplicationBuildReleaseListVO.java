package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 发布构建列表响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/24 9:54
 */
@Data
@ApiModel(value = "发布构建列表响应")
public class ApplicationBuildReleaseListVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "构建序列")
    private Integer seq;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
