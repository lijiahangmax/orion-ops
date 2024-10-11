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
package com.orion.ops.entity.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件 tail 对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/10 18:53
 */
@Data
@ApiModel(value = "文件 tail 对象")
@SuppressWarnings("ALL")
public class FileTailDTO {

    @ApiModelProperty(value = "文件绝对路径")
    private String filePath;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    /**
     * @see com.orion.ops.constant.system.SystemEnvAttr#TAIL_MODE
     * @see com.orion.ops.constant.tail.FileTailMode
     */
    @ApiModelProperty(value = "tail 模式")
    private String mode;

    /**
     * @see com.orion.ops.constant.machine.MachineEnvAttr#TAIL_OFFSET
     * @see com.orion.ops.constant.Const#TAIL_OFFSET_LINE
     */
    @ApiModelProperty(value = "尾行偏移量")
    private Integer offset;

    /**
     * @see com.orion.ops.constant.machine.MachineEnvAttr#TAIL_CHARSET
     * @see com.orion.ops.constant.Const#UTF_8
     */
    @ApiModelProperty(value = "编码集")
    private String charset;

    @ApiModelProperty(value = "命令")
    private String command;

}
