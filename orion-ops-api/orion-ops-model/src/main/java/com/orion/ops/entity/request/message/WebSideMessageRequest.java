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
package com.orion.ops.entity.request.message;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 站内信请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 16:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "站内信请求")
@SuppressWarnings("ALL")
public class WebSideMessageRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    /**
     * @see com.orion.ops.constant.message.MessageClassify
     */
    @ApiModelProperty(value = "消息分类")
    private Integer classify;

    /**
     * @see com.orion.ops.constant.message.MessageType
     */
    @ApiModelProperty(value = "消息类型")
    private Integer type;

    /**
     * @see com.orion.ops.constant.message.ReadStatus
     */
    @ApiModelProperty(value = "是否已读 1未读 2已读")
    private Integer status;

    @ApiModelProperty(value = "消息")
    private String message;

    @ApiModelProperty(value = "最大id")
    private Long maxId;

    @ApiModelProperty(value = "开始时间区间-开始")
    private Date rangeStart;

    @ApiModelProperty(value = "开始时间区间-结束")
    private Date rangeEnd;

}
