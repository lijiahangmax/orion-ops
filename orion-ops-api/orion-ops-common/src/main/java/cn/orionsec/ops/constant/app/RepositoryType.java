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
package cn.orionsec.ops.constant.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 应用版本仓库工具类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/6 10:48
 */
@AllArgsConstructor
@Getter
public enum RepositoryType {

    /**
     * git
     */
    GIT(1),

    ;

    private final Integer type;

    public static RepositoryType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (RepositoryType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
