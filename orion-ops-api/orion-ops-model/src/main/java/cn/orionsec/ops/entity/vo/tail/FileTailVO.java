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
package cn.orionsec.ops.entity.vo.tail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 文件tail响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/20 19:17
 */
@Data
@ApiModel(value = "文件tail响应")
@SuppressWarnings("ALL")
public class FileTailVO {

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器host")
    private String machineHost;

    /**
     * @see cn.orionsec.ops.constant.Const#ENABLE
     * @see cn.orionsec.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "机器状态 1有效 2无效")
    private Integer machineStatus;

    @ApiModelProperty(value = "文件路径")
    private String path;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * @see cn.orionsec.ops.constant.Const#TAIL_OFFSET_LINE
     */
    @ApiModelProperty(value = "偏移量")
    private Integer offset;

    /**
     * @see cn.orionsec.ops.constant.Const#UTF_8
     */
    @ApiModelProperty(value = "编码集")
    private String charset;

    @ApiModelProperty(value = "命令")
    private String command;

    /**
     * @see cn.orionsec.ops.constant.tail.FileTailMode
     */
    @ApiModelProperty(value = "宿主机文件追踪类型 tracker/tail")
    private String tailMode;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "更新时间")
    private String updateTimeAgo;

}
