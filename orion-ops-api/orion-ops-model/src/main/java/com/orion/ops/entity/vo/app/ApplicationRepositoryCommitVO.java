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
package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 分支提交信息响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 17:57
 */
@Data
@ApiModel(value = "分支提交信息响应")
public class ApplicationRepositoryCommitVO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "提交信息")
    private String message;

    @ApiModelProperty(value = "提交人")
    private String name;

    @ApiModelProperty(value = "提交时间")
    private Date time;

    @ApiModelProperty(value = "提交时间")
    private String timeAgo;

}
