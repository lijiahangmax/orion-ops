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
package cn.orionsec.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 机器信息表
 *
 * @author Jiahang Li
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "机器信息表")
@TableName("machine_info")
@SuppressWarnings("ALL")
public class MachineInfoDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "代理id")
    @TableField(value = "proxy_id", updateStrategy = FieldStrategy.IGNORED)
    private Long proxyId;

    @ApiModelProperty(value = "密钥id")
    @TableField(value = "key_id", updateStrategy = FieldStrategy.IGNORED)
    private Long keyId;

    @ApiModelProperty(value = "主机ip")
    @TableField("machine_host")
    private String machineHost;

    @ApiModelProperty(value = "ssh端口")
    @TableField("ssh_port")
    private Integer sshPort;

    @ApiModelProperty(value = "机器名称")
    @TableField("machine_name")
    private String machineName;

    @ApiModelProperty(value = "机器唯一标识")
    @TableField("machine_tag")
    private String machineTag;

    @ApiModelProperty(value = "机器描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "机器账号")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "机器密码")
    @TableField("password")
    private String password;

    /**
     * @see cn.orionsec.ops.constant.machine.MachineAuthType
     */
    @ApiModelProperty(value = "机器认证方式 1: 密码认证 2: 独立密钥")
    @TableField("auth_type")
    private Integer authType;

    /**
     * @see cn.orionsec.ops.constant.Const#ENABLE
     * @see cn.orionsec.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "机器状态 1有效 2无效")
    @TableField("machine_status")
    private Integer machineStatus;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
