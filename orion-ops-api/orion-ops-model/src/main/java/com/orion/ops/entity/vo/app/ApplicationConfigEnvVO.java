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
package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用配置环境响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/21 11:19
 */
@Data
@ApiModel(value = "应用配置环境响应")
@SuppressWarnings("ALL")
public class ApplicationConfigEnvVO {

    /**
     * @see com.orion.ops.constant.app.ApplicationEnvAttr#BUNDLE_PATH
     */
    @ApiModelProperty(value = "构建产物路径")
    private String bundlePath;

    /**
     * @see com.orion.ops.constant.app.ApplicationEnvAttr#TRANSFER_PATH
     */
    @ApiModelProperty(value = "产物传输绝对路径")
    private String transferPath;

    /**
     * @see com.orion.ops.constant.app.ApplicationEnvAttr#TRANSFER_MODE
     */
    @ApiModelProperty(value = "产物传输方式 (sftp/scp)")
    private String transferMode;

    /**
     * @see com.orion.ops.constant.app.ApplicationEnvAttr#TRANSFER_FILE_TYPE
     */
    @ApiModelProperty(value = "产物传输文件类型 (normal/zip)")
    private String transferFileType;

    /**
     * @see com.orion.ops.constant.app.ApplicationEnvAttr#RELEASE_SERIAL
     * @see com.orion.ops.constant.common.SerialType
     */
    @ApiModelProperty(value = "发布序列 10串行 20并行")
    private Integer releaseSerial;

    /**
     * @see com.orion.ops.constant.app.ApplicationEnvAttr#EXCEPTION_HANDLER
     * @see com.orion.ops.constant.common.ExceptionHandlerType
     * @see com.orion.ops.constant.common.SerialType#SERIAL
     */
    @ApiModelProperty(value = "异常处理 10跳过所有 20跳过错误")
    private Integer exceptionHandler;

}
