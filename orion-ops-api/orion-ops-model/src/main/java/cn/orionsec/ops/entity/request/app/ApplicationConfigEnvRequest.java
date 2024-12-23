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
package cn.orionsec.ops.entity.request.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用环境配置请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 18:50
 */
@Data
@ApiModel(value = "应用环境配置请求")
@SuppressWarnings("ALL")
public class ApplicationConfigEnvRequest {

    /**
     * @see cn.orionsec.ops.constant.app.ApplicationEnvAttr#BUNDLE_PATH
     */
    @ApiModelProperty(value = "构建产物路径")
    private String bundlePath;

    /**
     * @see cn.orionsec.ops.constant.app.ApplicationEnvAttr#TRANSFER_PATH
     */
    @ApiModelProperty(value = "产物传输绝对路径")
    private String transferPath;

    /**
     * @see cn.orionsec.ops.constant.app.ApplicationEnvAttr#TRANSFER_MODE
     */
    @ApiModelProperty(value = "产物传输方式 (sftp/scp)")
    private String transferMode;

    /**
     * @see cn.orionsec.ops.constant.app.ApplicationEnvAttr#TRANSFER_FILE_TYPE
     */
    @ApiModelProperty(value = "产物传输文件类型 (normal/zip)")
    private String transferFileType;

    /**
     * @see cn.orionsec.ops.constant.app.ApplicationEnvAttr#RELEASE_SERIAL
     * @see cn.orionsec.ops.constant.common.SerialType
     */
    @ApiModelProperty(value = "发布序列 10串行 20并行")
    private Integer releaseSerial;

    /**
     * @see cn.orionsec.ops.constant.app.ApplicationEnvAttr#EXCEPTION_HANDLER
     * @see cn.orionsec.ops.constant.common.ExceptionHandlerType
     * @see cn.orionsec.ops.constant.common.SerialType#SERIAL
     */
    @ApiModelProperty(value = "异常处理 10跳过所有 20跳过错误")
    private Integer exceptionHandler;

}
