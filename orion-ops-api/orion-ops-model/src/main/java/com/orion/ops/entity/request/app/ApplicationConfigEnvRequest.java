package com.orion.ops.entity.request.app;

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
