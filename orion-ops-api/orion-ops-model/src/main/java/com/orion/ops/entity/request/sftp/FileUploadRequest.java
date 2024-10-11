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
package com.orion.ops.entity.request.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * sftp 上传文件请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/28 18:31
 */
@Data
@ApiModel(value = "上传文件请求")
public class FileUploadRequest {

    @ApiModelProperty(value = "sessionToken")
    private String sessionToken;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "文件token")
    private String fileToken;

    @ApiModelProperty(value = "本地文件路径")
    private String localPath;

    @ApiModelProperty(value = "远程文件路径")
    private String remotePath;

    @ApiModelProperty(value = "文件大小")
    private Long size;

}
