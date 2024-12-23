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
package cn.orionsec.ops.entity.request.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件下载请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:08
 */
@Data
@ApiModel(value = "文件下载请求")
@SuppressWarnings("ALL")
public class FileDownloadRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * @see cn.orionsec.ops.constant.download.FileDownloadType
     */
    @ApiModelProperty(value = "下载类型")
    private Integer type;

}
