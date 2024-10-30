/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 命令模板表
 *
 * @author Jiahang Li
 * @since 2021-06-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "命令模板表")
@TableName("command_template")
public class CommandTemplateDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "模板名称")
    @TableField("template_name")
    private String templateName;

    @ApiModelProperty(value = "命令")
    @TableField("template_value")
    private String templateValue;

    @ApiModelProperty(value = "命令描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "创建用户id")
    @TableField("create_user_id")
    private Long createUserId;

    @ApiModelProperty(value = "创建用户名")
    @TableField("create_user_name")
    private String createUserName;

    @ApiModelProperty(value = "修改用户id")
    @TableField("update_user_id")
    private Long updateUserId;

    @ApiModelProperty(value = "修改用户名")
    @TableField("update_user_name")
    private String updateUserName;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
