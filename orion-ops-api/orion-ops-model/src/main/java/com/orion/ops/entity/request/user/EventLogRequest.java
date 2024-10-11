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
package com.orion.ops.entity.request.user;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 操作日志请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/10 16:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "操作日志请求")
@SuppressWarnings("ALL")
public class EventLogRequest extends PageRequest {

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * @see com.orion.ops.constant.event.EventClassify
     */
    @ApiModelProperty(value = "事件分类")
    private Integer classify;

    /**
     * @see com.orion.ops.constant.event.EventType
     */
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

    @ApiModelProperty(value = "开始时间区间-开始")
    private Date rangeStart;

    @ApiModelProperty(value = "开始时间区间-结束")
    private Date rangeEnd;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     */
    @ApiModelProperty(value = "只看自己")
    private Integer onlyMyself;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     */
    @ApiModelProperty(value = "是否解析ip")
    private Integer parserIp;

}
