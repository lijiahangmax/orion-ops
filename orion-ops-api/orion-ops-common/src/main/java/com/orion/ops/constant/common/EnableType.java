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
package com.orion.ops.constant.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 启用类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/16 9:21
 */
@Getter
@AllArgsConstructor
public enum EnableType {

    /**
     * 启用
     */
    ENABLED(Boolean.TRUE, "enabled"),

    /**
     * 停用
     */
    DISABLED(Boolean.FALSE, "disabled"),

    ;

    private final Boolean value;

    private final String label;

    public static EnableType of(String label) {
        if (label == null) {
            return DISABLED;
        }
        for (EnableType value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        return DISABLED;
    }

    public static EnableType of(Boolean value) {
        if (value == null) {
            return DISABLED;
        }
        for (EnableType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return DISABLED;
    }

}
