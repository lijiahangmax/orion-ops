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

import java.util.Date;

/**
 * 终端终端配置响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/2 21:02
 */
@Data
@ApiModel(value = "终端终端配置响应")
@SuppressWarnings("ALL")
public class MachineTerminalVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    /**
     * @see cn.orionsec.kit.net.host.ssh.TerminalType
     */
    @ApiModelProperty(value = "终端类型")
    private String terminalType;

    /**
     * @see cn.orionsec.ops.constant.terminal.TerminalConst#BACKGROUND_COLOR
     */
    @ApiModelProperty(value = "背景色")
    private String backgroundColor;

    /**
     * @see cn.orionsec.ops.constant.terminal.TerminalConst#FONT_COLOR
     */
    @ApiModelProperty(value = "字体颜色")
    private String fontColor;

    /**
     * @see cn.orionsec.ops.constant.terminal.TerminalConst#FONT_SIZE
     */
    @ApiModelProperty(value = "字体大小")
    private Integer fontSize;

    /**
     * @see cn.orionsec.ops.constant.terminal.TerminalConst#FONT_FAMILY
     */
    @ApiModelProperty(value = "字体名称")
    private String fontFamily;

    /**
     * @see cn.orionsec.ops.constant.Const#ENABLE
     * @see cn.orionsec.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "是否开启url link 1开启 2关闭")
    private Integer enableWebLink;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

}
