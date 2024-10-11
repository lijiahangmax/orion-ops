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
 * 应用发布明细响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/31 8:46
 */
@Data
@ApiModel(value = "应用发布明细响应")
@SuppressWarnings("ALL")
public class ApplicationReleaseStatisticsRecordVO {

    @ApiModelProperty(value = "发布id")
    private Long releaseId;

    @ApiModelProperty(value = "发布标题")
    private String releaseTitle;

    @ApiModelProperty(value = "发布时间")
    private Date releaseDate;

    /**
     * @see com.orion.ops.constant.app.ReleaseStatus
     */
    @ApiModelProperty(value = "发布状态 10待审核 20审核驳回 30待发布 35待调度 40发布中 50发布完成 60发布停止 70发布失败")
    private Integer status;

    @ApiModelProperty(value = "成功构建操作时长毫秒")
    private Long used;

    @ApiModelProperty(value = "成功构建操作时长")
    private String usedInterval;

    @ApiModelProperty(value = "发布机器")
    private List<ApplicationReleaseStatisticsMachineVO> machines;

}
