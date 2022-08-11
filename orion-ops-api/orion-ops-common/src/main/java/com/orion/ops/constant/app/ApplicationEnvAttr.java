package com.orion.ops.constant.app;

import lombok.Getter;

import java.util.Arrays;

/**
 * 应用env常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/4 11:29
 */
@Getter
public enum ApplicationEnvAttr {

    /**
     * 构建产物路径
     */
    BUNDLE_PATH("宿主机构建产物路径 (绝对路径/基于版本仓库的相对路径)"),

    /**
     * 产物传输路径
     */
    TRANSFER_PATH("产物传输目标机器绝对路径"),

    /**
     * 产物传输方式 (sftp/scp)
     *
     * @see TransferMode
     */
    TRANSFER_MODE("产物传输方式 (sftp/scp)"),

    /**
     * 产物传输文件类型 (normal/zip)
     *
     * @see TransferFileType
     */
    TRANSFER_FILE_TYPE("产物传输文件类型 (normal/zip)"),

    /**
     * 发布序列方式
     *
     * @see com.orion.ops.constant.SerialType
     */
    RELEASE_SERIAL("发布序列方式 (serial/parallel)"),

    /**
     * 异常处理类型
     *
     * @see com.orion.ops.constant.SerialType#SERIAL
     * @see com.orion.ops.constant.ExceptionHandlerType
     */
    EXCEPTION_HANDLER("异常处理类型 (skip_all/skip_error)"),

    /**
     * 构建序列号
     */
    BUILD_SEQ("构建序列号 (自增)"),

    ;

    /**
     * key
     */
    private final String key;

    /**
     * 描述
     */
    private final String description;

    ApplicationEnvAttr(String description) {
        this.description = description;
        this.key = this.name().toLowerCase();
    }

    public static ApplicationEnvAttr of(String key) {
        if (key == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.key.equals(key))
                .findFirst()
                .orElse(null);
    }

}
