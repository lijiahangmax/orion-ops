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
package com.orion.ops.entity.request.template;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 命令模板请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 18:29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "命令模板请求")
public class CommandTemplateRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "模板名称")
    private String name;

    @ApiModelProperty(value = "模板值")
    private String value;

    @ApiModelProperty(value = "模板描述")
    private String description;

    @ApiModelProperty(value = "是否省略值")
    private boolean omitValue;

}
