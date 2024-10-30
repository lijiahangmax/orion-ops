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
package cn.orionsec.ops.constant.sftp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * sftp 操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/26 12:08
 */
@Getter
@AllArgsConstructor
public enum SftpTransferType {

    /**
     * 10 上传
     */
    UPLOAD(10, "上传"),

    /**
     * 20 下载
     */
    DOWNLOAD(20, "下载"),

    /**
     * 30 传输
     */
    TRANSFER(30, "传输"),

    /**
     * 40 打包
     */
    PACKAGE(40, "打包"),

    ;

    private final Integer type;

    private final String label;

    public static SftpTransferType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (SftpTransferType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
