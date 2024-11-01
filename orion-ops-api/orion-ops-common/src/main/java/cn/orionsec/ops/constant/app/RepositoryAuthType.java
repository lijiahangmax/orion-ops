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

import cn.orionsec.ops.constant.CnConst;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 仓库认证方式
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/19 22:37
 */
@AllArgsConstructor
@Getter
public enum RepositoryAuthType {

    /**
     * 密码
     */
    PASSWORD(10, CnConst.PASSWORD),

    /**
     * 私人令牌
     */
    TOKEN(20, CnConst.TOKEN),

    ;

    private final Integer type;

    private final String label;

    public static RepositoryAuthType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (RepositoryAuthType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static RepositoryAuthType of(String label) {
        if (label == null) {
            return null;
        }
        for (RepositoryAuthType value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        return null;
    }

}
