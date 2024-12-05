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
package cn.orionsec.ops.entity.vo.upload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 批量上传获取token响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/16 10:44
 */
@Data
@ApiModel(value = "批量上传获取token响应")
public class BatchUploadTokenVO {

    @ApiModelProperty(value = "accessToken")
    private String accessToken;

    @ApiModelProperty(value = "notifyToken")
    private String notifyToken;

}