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
package cn.orionsec.ops.constant.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 树移动类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/28 17:42
 */
@Getter
@AllArgsConstructor
public enum TreeMoveType {

    /**
     * 移动到内层 上面
     */
    IN_TOP(1),

    /**
     * 移动到内层 下面
     */
    IN_BOTTOM(2),

    /**
     * 移动到节点 上面
     */
    PREV(3),

    /**
     * 移动到节点 下面
     */
    NEXT(4),

    ;

    private final Integer type;

    public static TreeMoveType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (TreeMoveType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
