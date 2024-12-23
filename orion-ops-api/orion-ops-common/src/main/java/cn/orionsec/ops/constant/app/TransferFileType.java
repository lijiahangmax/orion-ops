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

import lombok.Getter;

/**
 * 文件传输类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/21 10:17
 */
@Getter
public enum TransferFileType {

    /**
     * 文件 / 文件夹
     */
    NORMAL,

    /**
     * 文件夹 zip 文件
     */
    ZIP,

    ;

    TransferFileType() {
        this.value = name().toLowerCase();
    }

    private final String value;

    public static TransferFileType of(String value) {
        if (value == null) {
            return NORMAL;
        }
        for (TransferFileType type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return NORMAL;
    }

}
