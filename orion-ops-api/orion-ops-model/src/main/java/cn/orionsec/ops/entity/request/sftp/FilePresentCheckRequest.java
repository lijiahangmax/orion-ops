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
package cn.orionsec.ops.entity.request.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * sftp 检查文件是否存在
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/10/25 9:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "检查文件是否存在请求")
public class FilePresentCheckRequest extends FileBaseRequest {

    @ApiModelProperty(value = "当前路径")
    private String path;

    @ApiModelProperty(value = "文件名称")
    private List<String> names;

    @ApiModelProperty(value = "文件大小")
    private Long size;

}
