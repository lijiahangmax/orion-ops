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
 * 发布序列类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/2 15:20
 */
@AllArgsConstructor
@Getter
public enum SerialType {

    /**
     * 串行
     */
    SERIAL(10, "serial"),

    /**
     * 并行
     */
    PARALLEL(20, "parallel"),

    ;

    private final Integer type;

    private final String value;

    public static SerialType of(Integer type) {
        if (type == null) {
            return PARALLEL;
        }
        for (SerialType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return PARALLEL;
    }

    public static SerialType of(String value) {
        if (value == null) {
            return PARALLEL;
        }
        for (SerialType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return PARALLEL;
    }

}
