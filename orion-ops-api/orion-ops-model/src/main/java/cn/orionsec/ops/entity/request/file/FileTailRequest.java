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
package cn.orionsec.ops.entity.request.file;

import cn.orionsec.kit.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 文件tail请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/10 18:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "文件tail请求")
@SuppressWarnings("ALL")
public class FileTailRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "idList")
    private List<Long> idList;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "文件路径")
    private String path;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    /**
     * @see cn.orionsec.ops.constant.Const#TAIL_OFFSET_LINE
     */
    @ApiModelProperty(value = "文件尾部偏移行")
    private Integer offset;

    /**
     * @see cn.orionsec.ops.constant.Const#UTF_8
     */
    @ApiModelProperty(value = "编码集")
    private String charset;

    @ApiModelProperty(value = "tail命令")
    private String command;

    /**
     * @see cn.orionsec.ops.constant.tail.FileTailMode
     */
    @ApiModelProperty(value = "宿主机文件追踪类型 tracker/tail")
    private String tailMode;

    @ApiModelProperty(value = "relId")
    private Long relId;

    /**
     * @see cn.orionsec.ops.constant.tail.FileTailType
     */
    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "token")
    private String token;

}
