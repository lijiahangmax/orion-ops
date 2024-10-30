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
 * sftp打包传输类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/6 15:20
 */
@Getter
@AllArgsConstructor
public enum SftpPackageType {

    /**
     * 上传文件
     */
    UPLOAD(1),

    /**
     * 下载文件
     */
    DOWNLOAD(2),

    /**
     * 全部
     */
    ALL(3),

    ;

    private final Integer type;

    public static SftpPackageType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (SftpPackageType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
