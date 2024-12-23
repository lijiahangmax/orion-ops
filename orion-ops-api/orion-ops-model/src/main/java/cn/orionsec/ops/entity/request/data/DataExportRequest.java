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
package cn.orionsec.ops.entity.request.data;

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
     * @see cn.orionsec.ops.constant.ExportType
     */
    @ApiModelProperty(value = "导出类型")
    private Integer exportType;

    /**
     * @see cn.orionsec.ops.constant.Const#ENABLE
     * @see cn.orionsec.ops.constant.Const#DISABLE
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
     * @see cn.orionsec.ops.constant.Const#ENABLE
     */
    @ApiModelProperty(value = "只看自己")
    private Integer onlyMyself;

}
