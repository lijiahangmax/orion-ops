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
package cn.orionsec.ops.entity.request.machine;

import cn.orionsec.kit.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 机器信息请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/14 22:06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "机器信息请求")
@SuppressWarnings("ALL")
public class MachineInfoRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "idList")
    private List<Long> idList;

    @ApiModelProperty(value = "代理id")
    private Long proxyId;

    @ApiModelProperty(value = "密钥id")
    private Long keyId;

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

    @ApiModelProperty(value = "机器密码")
    private String password;

    /**
     * @see cn.orionsec.ops.constant.machine.MachineAuthType
     */
    @ApiModelProperty(value = "机器认证方式 1: 密码认证 2: 独立密钥")
    private Integer authType;

    /**
     * @see cn.orionsec.ops.constant.Const#ENABLE
     * @see cn.orionsec.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "机器状态 1有效 2无效")
    private Integer status;

    @ApiModelProperty(value = "是否查询分组")
    private Boolean queryGroup;

    @ApiModelProperty(value = "分组id")
    private List<Long> groupIdList;

}
