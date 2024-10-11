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
package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应用信息响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 18:58
 */
@Data
@ApiModel(value = "应用信息响应")
@SuppressWarnings("ALL")
public class ApplicationInfoVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "应用唯一标识")
    private String tag;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "应用版本仓库id")
    private Long repoId;

    @ApiModelProperty(value = "应用版本仓库名称")
    private String repoName;

    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * @see com.orion.ops.constant.Const#CONFIGURED
     * @see com.orion.ops.constant.Const#NOT_CONFIGURED
     */
    @ApiModelProperty(value = "是否已经配置 1已配置 2未配置")
    private Integer isConfig;

    @ApiModelProperty(value = "应用机器")
    private List<ApplicationMachineVO> machines;

}
