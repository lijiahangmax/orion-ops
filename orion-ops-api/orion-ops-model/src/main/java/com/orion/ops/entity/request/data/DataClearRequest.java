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
package com.orion.ops.entity.request.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 数据清理请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/24 16:36
 */
@Data
@ApiModel(value = "数据清理请求")
@SuppressWarnings("ALL")
public class DataClearRequest {

    @ApiModelProperty(value = "保留天数")
    private Integer reserveDay;

    @ApiModelProperty(value = "保留条数")
    private Integer reserveTotal;

    /**
     * @see com.orion.ops.constant.DataClearType
     */
    @ApiModelProperty(value = "清理类型")
    private Integer clearType;

    /**
     * @see com.orion.ops.constant.DataClearRange
     */
    @ApiModelProperty(value = "清理区间")
    private Integer range;

    @ApiModelProperty(value = "清理的引用id")
    private List<Long> relIdList;

    @ApiModelProperty(value = "引用id")
    private Long relId;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "只清理我创建的")
    private Integer iCreated;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "只清理我审核的")
    private Integer iAudited;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "只清理我执行的")
    private Integer iExecute;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "只清理未读的")
    private Integer onlyRead;

}
