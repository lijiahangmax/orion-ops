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
package cn.orionsec.ops.entity.vo.home;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 首页统计数量返回响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/20 14:08
 */
@Data
@ApiModel(value = "首页统计数量返回响应")
public class HomeStatisticsCountVO {

    @ApiModelProperty(value = "机器数量")
    private Integer machineCount;

    @ApiModelProperty(value = "环境数量")
    private Integer profileCount;

    @ApiModelProperty(value = "应用数量")
    private Integer appCount;

    @ApiModelProperty(value = "流水线数量")
    private Integer pipelineCount;

}
