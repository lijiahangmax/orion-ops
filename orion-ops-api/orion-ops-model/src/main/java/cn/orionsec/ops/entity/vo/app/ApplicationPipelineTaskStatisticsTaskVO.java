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
package cn.orionsec.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 应用流水线统计分析操作日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 15:01
 */
@Data
@ApiModel(value = "应用流水线统计分析操作日志响应")
@SuppressWarnings("ALL")
public class ApplicationPipelineTaskStatisticsTaskVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "执行时间")
    private Date execDate;

    /**
     * @see cn.orionsec.ops.constant.app.PipelineStatus
     */
    @ApiModelProperty(value = "执行状态 10待审核 20审核驳回 30待执行 35待调度 40执行中 50执行完成 60执行停止 70执行失败")
    private Integer status;

    @ApiModelProperty(value = "成功执行操作时长毫秒")
    private Long used;

    @ApiModelProperty(value = "成功执行操作时长")
    private String usedInterval;

    @ApiModelProperty(value = "执行操作")
    private List<ApplicationPipelineTaskStatisticsDetailVO> details;

}
