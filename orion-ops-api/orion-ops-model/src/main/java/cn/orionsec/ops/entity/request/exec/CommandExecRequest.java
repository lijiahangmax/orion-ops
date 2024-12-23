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
package cn.orionsec.ops.entity.request.exec;

import cn.orionsec.kit.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 批量执行请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 17:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "批量执行请求")
@SuppressWarnings("ALL")
public class CommandExecRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "执行机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "执行机器id")
    private List<Long> machineIdList;

    @ApiModelProperty(value = "执行主机")
    private String host;

    @ApiModelProperty(value = "命令")
    private String command;

    @ApiModelProperty(value = "执行人")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    /**
     * @see cn.orionsec.ops.constant.command.ExecStatus
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    /**
     * @see cn.orionsec.ops.constant.command.ExecType
     */
    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "退出码")
    private Integer exitCode;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "是否省略命令")
    private boolean omitCommand;

}
