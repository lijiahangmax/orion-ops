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
package com.orion.ops.entity.vo.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 站内信响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 16:25
 */
@Data
@ApiModel(value = "站内信响应")
@SuppressWarnings("ALL")
public class WebSideMessageVO {

    @ApiModelProperty(value = "id")
    private Long id;

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

    @ApiModelProperty(value = "收信人id")
    private Long toUserId;

    @ApiModelProperty(value = "收信人名称")
    private String toUserName;

    @ApiModelProperty(value = "消息")
    private String message;

    @ApiModelProperty(value = "消息关联id")
    private Long relId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
    private String createTimeAgo;

}
