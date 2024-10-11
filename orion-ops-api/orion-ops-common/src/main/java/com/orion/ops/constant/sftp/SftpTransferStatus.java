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
package com.orion.ops.constant.sftp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * sftp 传输状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/26 12:11
 */
@Getter
@AllArgsConstructor
public enum SftpTransferStatus {

    /**
     * 10 等待中
     */
    WAIT(10),

    /**
     * 20 进行中
     */
    RUNNABLE(20),

    /**
     * 30 已暂停
     */
    PAUSE(30),

    /**
     * 40 已完成
     */
    FINISH(40),

    /**
     * 50 已取消
     */
    CANCEL(50),

    /**
     * 60 传输异常
     */
    ERROR(60),

    ;

    private final Integer status;

    public static SftpTransferStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (SftpTransferStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
