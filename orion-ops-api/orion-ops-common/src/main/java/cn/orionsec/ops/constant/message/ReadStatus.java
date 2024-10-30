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
package cn.orionsec.ops.constant.message;

import cn.orionsec.ops.constant.CnConst;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 11:36
 */
@AllArgsConstructor
@Getter
public enum ReadStatus {

    /**
     * 未读
     */
    UNREAD(1, CnConst.UNREAD),

    /**
     * 已读
     */
    READ(2, CnConst.READ),

    ;

    private final Integer status;

    private final String label;

    public static ReadStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (ReadStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
