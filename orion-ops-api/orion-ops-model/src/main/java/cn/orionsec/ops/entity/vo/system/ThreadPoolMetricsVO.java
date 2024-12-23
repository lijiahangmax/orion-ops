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
package cn.orionsec.ops.entity.vo.system;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 线程池指标响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/23 18:17
 */
@Data
@ApiModel(value = "线程池指标响应")
@SuppressWarnings("ALL")
public class ThreadPoolMetricsVO {

    /**
     * @see cn.orionsec.ops.constant.system.ThreadPoolMetricsType
     */
    @ApiModelProperty(value = "type")
    private Integer type;

    @ApiModelProperty(value = "活跃线程数")
    private Integer activeThreadCount;

    @ApiModelProperty(value = "总任务数")
    private Long totalTaskCount;

    @ApiModelProperty(value = "已完成任务数")
    private Long completedTaskCount;

    @ApiModelProperty(value = "队列任务数")
    private Integer queueSize;

}
