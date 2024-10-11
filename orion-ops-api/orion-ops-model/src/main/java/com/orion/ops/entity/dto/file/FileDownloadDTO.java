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
 * 文件下载缓存对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:54
 */
@Data
@ApiModel(value = "文件下载缓存对象")
@SuppressWarnings("ALL")
public class FileDownloadDTO {

    @ApiModelProperty(value = "文件绝对路径")
    private String filePath;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "下载用户id")
    private Long userId;

    /**
     * @see com.orion.ops.constant.download.FileDownloadType
     */
    @ApiModelProperty(value = "下载类型")
    private Integer type;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

}
