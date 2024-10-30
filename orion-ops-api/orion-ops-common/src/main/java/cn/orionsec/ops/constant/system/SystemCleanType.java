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
package cn.orionsec.ops.constant.system;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 系统清理类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/17 11:38
 */
@AllArgsConstructor
@Getter
public enum SystemCleanType {

    /**
     * 临时文件
     */
    TEMP_FILE(10, "临时文件"),

    /**
     * 日志文件
     */
    LOG_FILE(20, "日志文件"),

    /**
     * 交换文件
     */
    SWAP_FILE(30, "交换文件"),

    /**
     * 历史产物文件
     */
    DIST_FILE(40, "旧版本构建产物"),

    /**
     * 版本仓库文件
     */
    REPO_FILE(50, "旧版本应用仓库"),

    /**
     * 录屏文件
     */
    SCREEN_FILE(60, "录屏文件"),

    ;

    private final Integer type;

    private final String label;

    public static SystemCleanType of(Integer type) {
        if (type == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.type.equals(type))
                .findFirst()
                .orElse(null);
    }

}
