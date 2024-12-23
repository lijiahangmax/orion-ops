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
package cn.orionsec.ops.entity.request.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应用配置请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 18:50
 */
@Data
@ApiModel(value = "应用配置请求")
@SuppressWarnings("ALL")
public class ApplicationConfigRequest {

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    /**
     * @see cn.orionsec.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型")
    private Integer stageType;

    @ApiModelProperty(value = "应用环境变量")
    private ApplicationConfigEnvRequest env;

    @ApiModelProperty(value = "机器id")
    private List<Long> machineIdList;

    @ApiModelProperty(value = "构建操作")
    private List<ApplicationConfigActionRequest> buildActions;

    @ApiModelProperty(value = "发布操作")
    private List<ApplicationConfigActionRequest> releaseActions;

}
