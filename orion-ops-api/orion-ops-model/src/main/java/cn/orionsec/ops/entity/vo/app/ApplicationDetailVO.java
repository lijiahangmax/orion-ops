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

import java.util.List;

/**
 * 应用详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 18:52
 */
@Data
@ApiModel(value = "应用详情响应")
public class ApplicationDetailVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "应用唯一标识")
    private String tag;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "应用版本仓库id")
    private Long repoId;

    @ApiModelProperty(value = "应用版本仓库名称")
    private String repoName;

    @ApiModelProperty(value = "配置环境变量")
    private ApplicationConfigEnvVO env;

    @ApiModelProperty(value = "构建流程")
    private List<ApplicationActionVO> buildActions;

    @ApiModelProperty(value = "关联机器")
    private List<ApplicationMachineVO> releaseMachines;

    @ApiModelProperty(value = "发布流程")
    private List<ApplicationActionVO> releaseActions;

}
