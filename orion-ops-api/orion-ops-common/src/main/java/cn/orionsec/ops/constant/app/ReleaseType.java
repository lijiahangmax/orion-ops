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
 * app发布类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/11 21:18
 */
@AllArgsConstructor
@Getter
public enum ReleaseType {

    /**
     * 正常发布
     */
    NORMAL(10),

    /**
     * 回滚发布
     */
    ROLLBACK(20),

    ;

    private final Integer type;

    public static ReleaseType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (ReleaseType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
