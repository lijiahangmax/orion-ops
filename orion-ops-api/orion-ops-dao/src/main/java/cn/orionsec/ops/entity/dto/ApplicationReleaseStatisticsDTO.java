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
package cn.orionsec.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 应用发布统计
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/30 17:19
 */
@Data
@ApiModel(value = "应用发布统计")
public class ApplicationReleaseStatisticsDTO {

    @ApiModelProperty(value = "发布次数")
    private Integer releaseCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
    private Integer failureCount;

    @ApiModelProperty(value = "日期")
    private Date date;

    @ApiModelProperty(value = "平均发布时长ms (成功)")
    private Long avgUsed;

}
