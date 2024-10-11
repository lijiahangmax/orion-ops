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
package com.orion.ops.constant.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审核状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/10 20:42
 */
@AllArgsConstructor
@Getter
public enum AuditStatus {

    /**
     * 通过
     */
    RESOLVE(10),

    /**
     * 驳回
     */
    REJECT(20),

    ;

    private final Integer status;

    public static AuditStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (AuditStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
