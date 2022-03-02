package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * app配置环境vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/21 11:19
 */
@Data
public class ApplicationConfigEnvVO {

    /**
     * 构建产物路径
     *
     * @see com.orion.ops.consts.app.ApplicationEnvAttr#BUNDLE_PATH
     */
    private String bundlePath;

    /**
     * 产物传输绝对路径
     *
     * @see com.orion.ops.consts.app.ApplicationEnvAttr#TRANSFER_PATH
     */
    private String transferPath;

    /**
     * 产物为文件夹传输类型 (dir/zip)
     *
     * @see com.orion.ops.consts.app.ApplicationEnvAttr#TRANSFER_DIR_TYPE
     */
    private String transferDirType;

    /**
     * 发布序列 10串行 20并行
     *
     * @see com.orion.ops.consts.app.ApplicationEnvAttr#RELEASE_SERIAL
     * @see com.orion.ops.consts.SerialType
     */
    private Integer releaseSerial;

    /**
     * 异常处理 10跳过所有 20跳过错误
     *
     * @see com.orion.ops.consts.app.ApplicationEnvAttr#EXCEPTION_HANDLER
     * @see com.orion.ops.consts.ExceptionHandlerType
     * @see com.orion.ops.consts.SerialType#SERIAL
     */
    private Integer exceptionHandler;

}
