package com.orion.ops.consts.download;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 下载类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:09
 */
@AllArgsConstructor
@Getter
public enum FileDownloadType {

    /**
     * 秘钥
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#KEY_PATH
     */
    SECRET_KEY(10, true),

    /**
     * terminal 日志
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#LOG_PATH
     */
    TERMINAL_LOG(20, true),

    /**
     * 命令 执行日志
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#LOG_PATH
     */
    EXEC_LOG(30, true),

    /**
     * sftp 下载文件
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#SWAP_PATH
     */
    SFTP_DOWNLOAD(40, true),

    /**
     * tail 列表文件
     */
    TAIL_LIST_FILE(50, false),

    /**
     * 应用构建日志
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#LOG_PATH
     */
    APP_BUILD_LOG(60, true),

    /**
     * 应用构建操作日志
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#LOG_PATH
     */
    APP_ACTION_LOG(70, true),

    /**
     * 应用构建 产物文件
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#DIST_PATH
     */
    APP_BUILD_BUNDLE(80, true),

    /**
     * 应用发布 机器日志
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#LOG_PATH
     */
    APP_RELEASE_MACHINE_LOG(90, true),

    /**
     * 调度任务机器日志
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#LOG_PATH
     */
    SCHEDULER_TASK_MACHINE_LOG(110, true),

    ;

    /**
     * 类型
     */
    private final Integer type;

    /**
     * 是否为本地文件
     */
    private final boolean isLocal;

    public static FileDownloadType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (FileDownloadType value : values()) {
            if (type.equals(value.type)) {
                return value;
            }
        }
        return null;
    }

}
