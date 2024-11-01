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
package cn.orionsec.ops.constant.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用版本仓库状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/28 14:06
 */
@AllArgsConstructor
@Getter
public enum RepositoryStatus {

    /**
     * 未初始化
     */
    UNINITIALIZED(10),

    /**
     * 初始化中
     */
    INITIALIZING(20),

    /**
     * 正常
     */
    OK(30),

    /**
     * 失败
     */
    ERROR(40),

    ;

    /**
     * 状态
     */
    private final Integer status;

    public static RepositoryStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (RepositoryStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
