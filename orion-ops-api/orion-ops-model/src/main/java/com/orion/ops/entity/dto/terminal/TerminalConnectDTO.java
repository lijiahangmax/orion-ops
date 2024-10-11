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
package com.orion.ops.entity.dto.terminal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 终端连接参数
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/17 20:12
 */
@Data
@ApiModel(value = "终端连接参数")
public class TerminalConnectDTO {

    @ApiModelProperty(value = "列数")
    private Integer cols;

    @ApiModelProperty(value = "行数")
    private Integer rows;

    @ApiModelProperty(value = "登陆 token")
    private String loginToken;

}
