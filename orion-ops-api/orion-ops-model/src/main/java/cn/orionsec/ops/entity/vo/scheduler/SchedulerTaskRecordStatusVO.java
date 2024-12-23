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
package cn.orionsec.ops.entity.vo.scheduler;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 调度机器执行状态响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/26 16:28
 */
@Data
@ApiModel(value = "调度机器执行状态响应")
@SuppressWarnings("ALL")
public class SchedulerTaskRecordStatusVO {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * @see cn.orionsec.ops.constant.scheduler.SchedulerTaskStatus
     */
    @ApiModelProperty(value = "调度状态")
    private Integer status;

    @ApiModelProperty(value = "执行开始时间")
    private Date startTime;

    @ApiModelProperty(value = "执行开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "执行结束时间")
    private Date endTime;

    @ApiModelProperty(value = "执行结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "机器状态")
    private List<SchedulerTaskMachineRecordStatusVO> machines;

}
