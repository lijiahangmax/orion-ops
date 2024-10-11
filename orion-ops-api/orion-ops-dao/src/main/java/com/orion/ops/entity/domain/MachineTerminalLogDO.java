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
package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 机器终端操作日志
 *
 * @author Jiahang Li
 * @since 2021-04-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "机器终端操作日志")
@TableName("machine_terminal_log")
@SuppressWarnings("ALL")
public class MachineTerminalLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "机器id")
    @TableField("machine_id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    @TableField("machine_name")
    private String machineName;

    @ApiModelProperty(value = "机器唯一标识")
    @TableField("machine_tag")
    private String machineTag;

    @ApiModelProperty(value = "机器host")
    @TableField("machine_host")
    private String machineHost;

    @ApiModelProperty(value = "token")
    @TableField("access_token")
    private String accessToken;

    @ApiModelProperty(value = "建立连接时间")
    @TableField("connected_time")
    private Date connectedTime;

    @ApiModelProperty(value = "断开连接时间")
    @TableField("disconnected_time")
    private Date disconnectedTime;

    /**
     * @see com.orion.ops.constant.ws.WsCloseCode
     */
    @ApiModelProperty(value = "close code")
    @TableField("close_code")
    private Integer closeCode;

    @ApiModelProperty(value = "录屏文件路径")
    @TableField("screen_path")
    private String screenPath;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
