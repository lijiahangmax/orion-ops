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
package com.orion.ops.entity.vo.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 机器终端管理响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 22:42
 */
@Data
@ApiModel(value = "机器终端管理响应")
public class MachineTerminalManagerVO {

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "username")
    private String userName;

    @ApiModelProperty(value = "连接时间")
    private Date connectedTime;

    @ApiModelProperty(value = "连接时间")
    private String connectedTimeAgo;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器host")
    private String machineHost;

    @ApiModelProperty(value = "机器唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "logId")
    private Long logId;

}
