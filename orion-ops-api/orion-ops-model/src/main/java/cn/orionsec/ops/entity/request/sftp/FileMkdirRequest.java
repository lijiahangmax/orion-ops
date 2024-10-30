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
package cn.orionsec.ops.entity.request.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sftp 创建文件夹请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 20:08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "创建文件夹请求")
public class FileMkdirRequest extends FileBaseRequest {

    @ApiModelProperty(value = "绝对路径")
    private String path;

}
