package com.orion.ops.constant.download;

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
     * 密钥
     *
     * @see com.orion.ops.constant.system.SystemEnvAttr#KEY_PATH
     */
    SECRET_KEY(10),

    /**
     * terminal 录屏
     *
     * @see com.orion.ops.constant.system.SystemEnvAttr#LOG_PATH
     */
    TERMINAL_SCREEN(20),

    /**
     * 命令 执行日志
     *
     * @see com.orion.ops.constant.system.SystemEnvAttr#LOG_PATH
     */
    EXEC_LOG(30),

    /**
     * sftp 下载文件
     *
     * @see com.orion.ops.constant.system.SystemEnvAttr#SWAP_PATH
     */
    SFTP_DOWNLOAD(40),

    /**
     * tail 列表文件
     */
    TAIL_LIST_FILE(50),

    /**
     * 应用构建日志
     *
     * @see com.orion.ops.constant.system.SystemEnvAttr#LOG_PATH
     */
    APP_BUILD_LOG(60),

    /**
     * 应用构建操作日志
     *
     * @see com.orion.ops.constant.system.SystemEnvAttr#LOG_PATH
     */
    APP_ACTION_LOG(70),

    /**
     * 应用构建 产物文件
     *
     * @see com.orion.ops.constant.system.SystemEnvAttr#DIST_PATH
     */
    APP_BUILD_BUNDLE(80),

    /**
     * 应用发布 机器日志
     *
     * @see com.orion.ops.constant.system.SystemEnvAttr#LOG_PATH
     */
    APP_RELEASE_MACHINE_LOG(90),

    /**
     * 调度任务机器日志
     *
     * @see com.orion.ops.constant.system.SystemEnvAttr#LOG_PATH
     */
    SCHEDULER_TASK_MACHINE_LOG(110),

    ;

    /**
     * 类型
     */
    private final Integer type;

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
