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
package cn.orionsec.ops.entity.vo.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 终端日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 20:59
 */
@Data
@ApiModel(value = "终端日志响应")
@SuppressWarnings("ALL")
public class MachineTerminalLogVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "机器host")
    private String machineHost;

    @ApiModelProperty(value = "访问token")
    private String accessToken;

    @ApiModelProperty(value = "建立连接时间")
    private Date connectedTime;

    @ApiModelProperty(value = "断开连接时间")
    private Date disconnectedTime;

    @ApiModelProperty(value = "建立连接时间")
    private String connectedTimeAgo;

    @ApiModelProperty(value = "断开连接时间")
    private String disconnectedTimeAgo;

    /**
     * @see cn.orionsec.ops.constant.ws.WsCloseCode
     */
    @ApiModelProperty(value = "close code")
    private Integer closeCode;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
