/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
package cn.orionsec.ops.entity.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 系统环境变量响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 17:36
 */
@Data
@ApiModel(value = "系统环境变量响应")
@SuppressWarnings("ALL")
public class SystemEnvVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "key")
    private String key;

    @ApiModelProperty(value = "value")
    private String value;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    /**
     * @see cn.orionsec.ops.constant.Const#FORBID_DELETE_CAN
     * @see cn.orionsec.ops.constant.Const#FORBID_DELETE_NOT
     */
    @ApiModelProperty(value = "是否禁止删除 1可以删除 2禁止删除")
    private Integer forbidDelete;

}
