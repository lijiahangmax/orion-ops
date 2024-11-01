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
package cn.orionsec.ops.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据清理区间
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/24 16:29
 */
@Getter
@AllArgsConstructor
public enum DataClearRange {

    /**
     * 保留天数
     */
    DAY(10),

    /**
     * 保留条数
     */
    TOTAL(20),

    /**
     * 关联数据
     */
    REL_ID(30),

    ;

    private final Integer range;

    public static DataClearRange of(Integer range) {
        if (range == null) {
            return null;
        }
        for (DataClearRange value : values()) {
            if (value.range.equals(range)) {
                return value;
            }
        }
        return null;
    }

}
