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
package com.orion.ops.constant.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 定时类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/20 9:09
 */
@AllArgsConstructor
@Getter
public enum TimedType {

    /**
     * 普通
     */
    NORMAL(10),

    /**
     * 定时
     */
    TIMED(20),

    ;

    private final Integer type;

    public static TimedType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (TimedType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
