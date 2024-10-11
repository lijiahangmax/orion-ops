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

/**
 * 数据导出请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 16:10
 */
@Data
@ApiModel(value = "数据导出请求")
@SuppressWarnings("ALL")
public class DataExportRequest {

    /**
     * @see com.orion.ops.constant.ExportType
     */
    @ApiModelProperty(value = "导出类型")
    private Integer exportType;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "是否导出密码")
    private Integer exportPassword;

    @ApiModelProperty(value = "保护密码")
    private String protectPassword;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "分类")
    private Integer classify;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     */
    @ApiModelProperty(value = "只看自己")
    private Integer onlyMyself;

}
