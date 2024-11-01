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
 * 用户事件日志
 *
 * @author Jiahang Li
 * @since 2021-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用户事件日志")
@TableName("user_event_log")
@SuppressWarnings("ALL")
public class UserEventLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    @TableField("username")
    private String username;

    /**
     * @see cn.orionsec.ops.constant.event.EventClassify
     */
    @ApiModelProperty(value = "事件分类")
    @TableField("event_classify")
    private Integer eventClassify;

    /**
     * @see cn.orionsec.ops.constant.event.EventType
     */
    @ApiModelProperty(value = "事件类型")
    @TableField("event_type")
    private Integer eventType;

    @ApiModelProperty(value = "日志信息")
    @TableField("log_info")
    private String logInfo;

    @ApiModelProperty(value = "日志参数")
    @TableField("params_json")
    private String paramsJson;

    /**
     * @see cn.orionsec.ops.constant.Const#ENABLE
     * @see cn.orionsec.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "是否执行成功 1成功 2失败")
    @TableField("exec_result")
    private Integer execResult;

    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

}
