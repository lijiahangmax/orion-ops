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

/**
 * 调度机器执行明细响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/25 14:42
 */
@Data
@ApiModel(value = "调度机器执行明细响应")
@SuppressWarnings("ALL")
public class SchedulerTaskMachineRecordVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "明细id")
    private Long recordId;

    @ApiModelProperty(value = "执行机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器主机")
    private String machineHost;

    @ApiModelProperty(value = "机器唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "执行命令")
    private String command;

    /**
     * @see cn.orionsec.ops.constant.scheduler.SchedulerTaskMachineStatus
     */
    @ApiModelProperty(value = "执行状态 10待调度 20调度中 30调度成功 40调度失败 50已跳过 60已停止")
    private Integer status;

    @ApiModelProperty(value = "退出码")
    private Integer exitCode;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

}
