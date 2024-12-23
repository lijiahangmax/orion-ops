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
package cn.orionsec.ops.entity.vo.machine;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 访问终端响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/1 16:40
 */
@Data
@ApiModel(value = "访问终端响应")
@SuppressWarnings("ALL")
public class TerminalAccessVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "主机")
    private String host;

    @ApiModelProperty(value = "端口")
    private Integer port;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "username")
    private String username;

    /**
     * @see cn.orionsec.kit.net.host.ssh.TerminalType
     */
    @ApiModelProperty(value = "终端类型")
    private String terminalType;

    @ApiModelProperty(value = "背景色")
    private String backgroundColor;

    @ApiModelProperty(value = "字体颜色")
    private String fontColor;

    @ApiModelProperty(value = "字体大小")
    private Integer fontSize;

    @ApiModelProperty(value = "字体名称")
    private String fontFamily;

    @ApiModelProperty(value = "访问token")
    private String accessToken;

    /**
     * @see cn.orionsec.ops.constant.Const#ENABLE
     * @see cn.orionsec.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "是否开启url link 1开启 2关闭")
    private Integer enableWebLink;

}
