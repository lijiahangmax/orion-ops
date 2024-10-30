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
package cn.orionsec.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用关联机器信息响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 19:07
 */
@Data
@ApiModel(value = "应用关联机器信息响应")
public class ApplicationMachineVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器主机")
    private String machineHost;

    @ApiModelProperty(value = "唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "当前版本发布id")
    private Long releaseId;

    @ApiModelProperty(value = "当前版本构建id")
    private Long buildId;

    @ApiModelProperty(value = "当前版本构建序列")
    private Integer buildSeq;

}
