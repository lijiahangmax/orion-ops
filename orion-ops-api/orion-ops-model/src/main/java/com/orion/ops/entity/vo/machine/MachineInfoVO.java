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
import java.util.List;

/**
 * 机器信息响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/14 22:10
 */
@Data
@ApiModel(value = "机器信息响应")
@SuppressWarnings("ALL")
public class MachineInfoVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "代理id")
    private Long proxyId;

    @ApiModelProperty(value = "代理主机")
    private String proxyHost;

    @ApiModelProperty(value = "代理端口")
    private Integer proxyPort;

    @ApiModelProperty(value = "代理类型")
    private Integer proxyType;

    @ApiModelProperty(value = "密钥id")
    private Long keyId;

    @ApiModelProperty(value = "密钥名称")
    private String keyName;

    @ApiModelProperty(value = "主机ip")
    private String host;

    @ApiModelProperty(value = "ssh端口")
    private Integer sshPort;

    @ApiModelProperty(value = "机器名称")
    private String name;

    @ApiModelProperty(value = "机器唯一标识")
    private String tag;

    @ApiModelProperty(value = "机器描述")
    private String description;

    @ApiModelProperty(value = "机器账号")
    private String username;

    /**
     * @see com.orion.ops.constant.machine.MachineAuthType
     */
    @ApiModelProperty(value = "机器认证方式 1: 密码认证 2: 独立密钥")
    private Integer authType;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "机器状态 1有效 2无效")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "分组id")
    private List<Long> groupIdList;

}
