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
package cn.orionsec.ops.constant.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息分类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 11:35
 */
@AllArgsConstructor
@Getter
public enum MessageClassify {

    /**
     * 系统消息
     */
    SYSTEM(10, "系统消息"),

    /**
     * 数据导入
     */
    IMPORT(20, "数据导入"),

    /**
     * 系统报警
     */
    ALARM(30, "系统报警"),

    ;

    private final Integer classify;

    private final String label;

    public static MessageClassify of(Integer classify) {
        if (classify == null) {
            return null;
        }
        for (MessageClassify value : values()) {
            if (value.classify.equals(classify)) {
                return value;
            }
        }
        return null;
    }

}
