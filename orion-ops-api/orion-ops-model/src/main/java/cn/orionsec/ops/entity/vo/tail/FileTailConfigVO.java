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
package cn.orionsec.ops.entity.vo.tail;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * tail配置响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/2 0:50
 */
@Data
@ApiModel(value = "tail配置响应")
@SuppressWarnings("ALL")
public class FileTailConfigVO {

    /**
     * @see cn.orionsec.ops.constant.Const#TAIL_OFFSET_LINE
     */
    @ApiModelProperty(value = "偏移量")
    private Integer offset;

    /**
     * @see cn.orionsec.ops.constant.Const#UTF_8
     */
    @ApiModelProperty(value = "文件编码")
    private String charset;

    /**
     * @see cn.orionsec.ops.constant.machine.MachineEnvAttr#TAIL_DEFAULT_COMMAND
     */
    @ApiModelProperty(value = "命令")
    private String command;

}
