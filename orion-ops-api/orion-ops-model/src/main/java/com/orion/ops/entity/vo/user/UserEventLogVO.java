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
package com.orion.ops.entity.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 操作日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/10 16:20
 */
@Data
@ApiModel(value = "操作日志响应")
@SuppressWarnings("ALL")
public class UserEventLogVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "事件分类")
    private Integer classify;

    @ApiModelProperty(value = "事件类型")
    private Integer type;

    @ApiModelProperty(value = "日志信息")
    private String log;

    @ApiModelProperty(value = "日志参数")
    private String params;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "是否执行成功 1成功 2失败")
    private Integer result;

    @ApiModelProperty(value = "操作时间")
    private Date createTime;

    @ApiModelProperty(value = "操作时间")
    private String createTimeAgo;

    @ApiModelProperty(value = "操作ip")
    private String ip;

    @ApiModelProperty(value = "操作ip位置")
    private String ipLocation;

}
