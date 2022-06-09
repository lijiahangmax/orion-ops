package com.orion.ops.entity.vo;

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
public class ApplicationConfigEnvVO {

    /**
     * @see com.orion.ops.consts.app.ApplicationEnvAttr#BUNDLE_PATH
     */
    @ApiModelProperty(value = "构建产物路径")
    private String bundlePath;

    /**
     * @see com.orion.ops.consts.app.ApplicationEnvAttr#TRANSFER_PATH
     */
    @ApiModelProperty(value = "产物传输绝对路径")
    private String transferPath;

    /**
     * @see com.orion.ops.consts.app.ApplicationEnvAttr#TRANSFER_MODE
     */
    @ApiModelProperty(value = "产物传输方式 (sftp/scp)")
    private String transferMode;

    /**
     * @see com.orion.ops.consts.app.ApplicationEnvAttr#TRANSFER_FILE_TYPE
     */
    @ApiModelProperty(value = "产物传输文件类型 (normal/zip)")
    private String transferFileType;

    /**
     * @see com.orion.ops.consts.app.ApplicationEnvAttr#RELEASE_SERIAL
     * @see com.orion.ops.consts.SerialType
     */
    @ApiModelProperty(value = "发布序列 10串行 20并行")
    private Integer releaseSerial;

    /**
     * @see com.orion.ops.consts.app.ApplicationEnvAttr#EXCEPTION_HANDLER
     * @see com.orion.ops.consts.ExceptionHandlerType
     * @see com.orion.ops.consts.SerialType#SERIAL
     */
    @ApiModelProperty(value = "异常处理 10跳过所有 20跳过错误")
    private Integer exceptionHandler;

}
