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
package com.orion.ops.entity.vo.machine;

import com.alibaba.fastjson.annotation.JSONField;
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

    @JSONField(name = "key")
    @ApiModelProperty(value = "id")
    private Long id;

    @JSONField(serialize = false)
    @ApiModelProperty(value = "父id")
    private Long parentId;

    @ApiModelProperty(value = "名称")
    private String title;

    @JSONField(serialize = false)
    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "子节点")
    private List<MachineGroupTreeVO> children;

}
