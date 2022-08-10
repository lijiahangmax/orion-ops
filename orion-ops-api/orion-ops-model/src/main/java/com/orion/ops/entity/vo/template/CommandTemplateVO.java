package com.orion.ops.entity.vo.template;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 命令模板响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 18:29
 */
@Data
@ApiModel(value = "命令模板响应")
public class CommandTemplateVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "模板名称")
    private String name;

    @ApiModelProperty(value = "模板值")
    private String value;

    @ApiModelProperty(value = "命令描述")
    private String description;

    @ApiModelProperty(value = "创建用户id")
    private Long createUserId;

    @ApiModelProperty(value = "创建用户名")
    private String createUserName;

    @ApiModelProperty(value = "修改用户id")
    private Long updateUserId;

    @ApiModelProperty(value = "修改用户名")
    private String updateUserName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
    private String createTimeAgo;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "修改时间")
    private String updateTimeAgo;

}
