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
package cn.orionsec.ops.entity.vo.upload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 批量上传文件检查响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 16:29
 */
@Data
@ApiModel(value = "批量上传文件检查响应")
public class BatchUploadCheckVO {

    @ApiModelProperty(value = "可连接的机器id")
    private Set<Long> connectedMachineIdList;

    @ApiModelProperty(value = "可以连接的机器")
    private List<BatchUploadCheckMachineVO> connectedMachines;

    @ApiModelProperty(value = "无法连接的机器")
    private List<BatchUploadCheckMachineVO> notConnectedMachines;

    @ApiModelProperty(value = "机器已存在的文件")
    private List<BatchUploadCheckFileVO> machinePresentFiles;

}
