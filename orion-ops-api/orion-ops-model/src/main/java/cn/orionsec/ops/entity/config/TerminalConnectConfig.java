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
package cn.orionsec.ops.entity.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 终端连接配置项
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 22:06
 */
@Data
@ApiModel(value = "终端连接配置项")
public class TerminalConnectConfig {

    @ApiModelProperty(value = "行数")
    private Integer rows;

    @ApiModelProperty(value = "列数")
    private Integer cols;

    /**
     * @see cn.orionsec.kit.net.host.ssh.TerminalType
     */
    @ApiModelProperty(value = "终端类型")
    private String terminalType;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器host")
    private String machineHost;

    @ApiModelProperty(value = "机器唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "连接时间")
    private Date connectedTime;

    @ApiModelProperty(value = "日志id")
    private Long logId;

}
