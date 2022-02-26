package com.orion.ops.entity.request;

import lombok.Data;

/**
 * app配置环境请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 18:50
 */
@Data
public class ApplicationConfigEnvRequest {

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

}
