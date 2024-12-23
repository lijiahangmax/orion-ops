/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
package cn.orionsec.ops.constant.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异常处理类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/22 10:02
 */
@Getter
@AllArgsConstructor
public enum ExceptionHandlerType {

    /**
     * 跳过所有 中断执行
     */
    SKIP_ALL(10, "skip_all"),

    /**
     * 跳过错误 继续执行
     */
    SKIP_ERROR(20, "skip_error"),

    ;

    private final Integer type;

    private final String value;

    public static ExceptionHandlerType of(Integer type) {
        if (type == null) {
            return ExceptionHandlerType.SKIP_ALL;
        }
        for (ExceptionHandlerType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return ExceptionHandlerType.SKIP_ALL;
    }

    public static ExceptionHandlerType of(String value) {
        if (value == null) {
            return ExceptionHandlerType.SKIP_ALL;
        }
        for (ExceptionHandlerType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return ExceptionHandlerType.SKIP_ALL;
    }

}
