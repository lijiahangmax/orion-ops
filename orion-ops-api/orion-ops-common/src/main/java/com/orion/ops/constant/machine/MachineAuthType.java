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
package com.orion.ops.constant.machine;

import com.orion.ops.constant.CnConst;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 认证类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/14 22:29
 */
@Getter
@AllArgsConstructor
public enum MachineAuthType {

    /**
     * 1 密码认证
     */
    PASSWORD(1, CnConst.PASSWORD),

    /**
     * 2 独立密钥
     */
    SECRET_KEY(2, CnConst.SECRET_KEY),

    ;

    private final Integer type;

    private final String label;

    public static MachineAuthType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (MachineAuthType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static MachineAuthType of(String label) {
        if (label == null) {
            return null;
        }
        for (MachineAuthType value : values()) {
            if (value.label.equals(label)) {
                return value;
            }
        }
        return null;
    }

}
