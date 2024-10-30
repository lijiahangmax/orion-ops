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
package cn.orionsec.ops.entity.request.app;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 应用构建请求
 *
 * @author Jiahang Li
 * @since 2021-12-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用构建请求")
@SuppressWarnings("ALL")
public class ApplicationBuildRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    @ApiModelProperty(value = "构建序列")
    private Integer seq;

    @ApiModelProperty(value = "构建分支")
    private String branchName;

    @ApiModelProperty(value = "构建提交id")
    private String commitId;

    /**
     * @see cn.orionsec.ops.constant.app.BuildStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20执行中 30已完成 40执行失败 50已取消")
    private Integer status;

    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * @see cn.orionsec.ops.constant.Const#ENABLE
     */
    @ApiModelProperty(value = "只看自己")
    private Integer onlyMyself;

    @ApiModelProperty(value = "命令")
    private String command;

}
