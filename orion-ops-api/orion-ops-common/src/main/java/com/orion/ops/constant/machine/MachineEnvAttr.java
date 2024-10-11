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
package com.orion.ops.constant.machine;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 目标机器机器环境变量key
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/3/29 15:42
 */
@Getter
public enum MachineEnvAttr {

    /**
     * sftp 文件名称编码格式
     *
     * @see com.orion.ops.constant.Const#UTF_8
     */
    SFTP_CHARSET("SFTP 文件名称编码格式"),

    /**
     * 文件追踪偏移量
     *
     * @see com.orion.ops.constant.Const#TAIL_OFFSET_LINE
     */
    TAIL_OFFSET("文件追踪偏移量(行)"),

    /**
     * 文件追踪编码格式
     *
     * @see com.orion.ops.constant.Const#UTF_8
     */
    TAIL_CHARSET("文件追踪编码格式"),

    /**
     * 文件追踪默认命令
     *
     * @see com.orion.ops.constant.command.CommandConst#TAIL_FILE_DEFAULT
     */
    TAIL_DEFAULT_COMMAND("文件追踪默认命令"),

    /**
     * 连接超时时间 (ms)
     *
     * @see MachineConst#CONNECT_TIMEOUT
     */
    CONNECT_TIMEOUT("连接超时时间 (ms)"),

    /**
     * 连接失败重试次数
     *
     * @see MachineConst#CONNECT_RETRY_TIMES
     */
    CONNECT_RETRY_TIMES("连接失败重试次数"),

    ;

    /**
     * key
     */
    private final String key;

    /**
     * 描述
     */
    private final String description;

    MachineEnvAttr(String description) {
        this.description = description;
        this.key = this.name().toLowerCase();
    }

    public static List<String> getKeys() {
        return Arrays.stream(values())
                .map(MachineEnvAttr::getKey)
                .collect(Collectors.toList());
    }

    public static MachineEnvAttr of(String key) {
        if (key == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.key.equals(key))
                .findFirst()
                .orElse(null);
    }

}
