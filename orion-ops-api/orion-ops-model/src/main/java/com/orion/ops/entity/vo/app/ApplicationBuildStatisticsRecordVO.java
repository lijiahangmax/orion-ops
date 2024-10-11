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

import java.util.Date;
import java.util.List;

/**
 * 应用构建统计分析操作日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 18:07
 */
@Data
@ApiModel(value = "应用构建统计分析操作日志响应")
@SuppressWarnings("ALL")
public class ApplicationBuildStatisticsRecordVO {

    @ApiModelProperty(value = "构建id")
    private Long buildId;

    @ApiModelProperty(value = "构建序列")
    private Integer seq;

    @ApiModelProperty(value = "构建时间")
    private Date buildDate;

    /**
     * @see com.orion.ops.constant.app.BuildStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20执行中 30已完成 40执行失败 50已取消")
    private Integer status;

    @ApiModelProperty(value = "成功构建操作时长毫秒")
    private Long used;

    @ApiModelProperty(value = "成功构建操作时长")
    private String usedInterval;

    @ApiModelProperty(value = "构建操作")
    private List<ApplicationActionLogStatisticsVO> actionLogs;

}
