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
package cn.orionsec.ops.constant.download;

import cn.orionsec.ops.constant.system.SystemEnvAttr;
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
     * @see SystemEnvAttr#KEY_PATH
     */
    SECRET_KEY(10),

    /**
     * terminal 录屏
     *
     * @see SystemEnvAttr#LOG_PATH
     */
    TERMINAL_SCREEN(20),

    /**
     * 命令 执行日志
     *
     * @see SystemEnvAttr#LOG_PATH
     */
    EXEC_LOG(30),

    /**
     * sftp 下载文件
     *
     * @see SystemEnvAttr#SWAP_PATH
     */
    SFTP_DOWNLOAD(40),

    /**
     * tail 列表文件
     */
    TAIL_LIST_FILE(50),

    /**
     * 应用构建日志
     *
     * @see SystemEnvAttr#LOG_PATH
     */
    APP_BUILD_LOG(60),

    /**
     * 应用构建操作日志
     *
     * @see SystemEnvAttr#LOG_PATH
     */
    APP_ACTION_LOG(70),

    /**
     * 应用构建 产物文件
     *
     * @see SystemEnvAttr#DIST_PATH
     */
    APP_BUILD_BUNDLE(80),

    /**
     * 应用发布 机器日志
     *
     * @see SystemEnvAttr#LOG_PATH
     */
    APP_RELEASE_MACHINE_LOG(90),

    /**
     * 调度任务机器日志
     *
     * @see SystemEnvAttr#LOG_PATH
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
