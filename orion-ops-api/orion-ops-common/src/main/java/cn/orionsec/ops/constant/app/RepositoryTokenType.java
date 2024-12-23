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

import cn.orionsec.ops.constant.Const;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 仓库令牌类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/19 22:38
 */
@AllArgsConstructor
@Getter
public enum RepositoryTokenType {

    /**
     * github
     * <p>
     * username: ''
     */
    GITHUB(10, Const.GITHUB),

    /**
     * gitee
     * <p>
     * username: username
     */
    GITEE(20, Const.GITEE),

    /**
     * gitlab
     * <p>
     * username oauth
     */
    GITLAB(30, Const.GITLAB),

    ;

    private final Integer type;

    private final String label;

    public static RepositoryTokenType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (RepositoryTokenType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static RepositoryTokenType of(String label) {
        if (label == null) {
            return null;
        }
        for (RepositoryTokenType value : values()) {
            if (value.label.equals(label.toLowerCase())) {
                return value;
            }
        }
        return null;
    }

}
