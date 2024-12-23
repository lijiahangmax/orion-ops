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

import cn.orionsec.ops.constant.common.ExceptionHandlerType;
import cn.orionsec.ops.constant.common.SerialType;
import lombok.Getter;

import java.util.Arrays;

/**
 * 应用env常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/4 11:29
 */
@Getter
public enum ApplicationEnvAttr {

    /**
     * 构建产物路径
     */
    BUNDLE_PATH("宿主机构建产物路径 (绝对路径/基于版本仓库的相对路径)"),

    /**
     * 产物传输路径
     */
    TRANSFER_PATH("产物传输目标机器绝对路径"),

    /**
     * 产物传输方式 (sftp/scp)
     *
     * @see TransferMode
     */
    TRANSFER_MODE("产物传输方式 (sftp/scp)"),

    /**
     * 产物传输文件类型 (normal/zip)
     *
     * @see TransferFileType
     */
    TRANSFER_FILE_TYPE("产物传输文件类型 (normal/zip)"),

    /**
     * 发布序列方式
     *
     * @see SerialType
     */
    RELEASE_SERIAL("发布序列方式 (serial/parallel)"),

    /**
     * 异常处理类型
     *
     * @see SerialType#SERIAL
     * @see ExceptionHandlerType
     */
    EXCEPTION_HANDLER("异常处理类型 (skip_all/skip_error)"),

    /**
     * 构建序列号
     */
    BUILD_SEQ("构建序列号 (自增)"),

    ;

    /**
     * key
     */
    private final String key;

    /**
     * 描述
     */
    private final String description;

    ApplicationEnvAttr(String description) {
        this.description = description;
        this.key = this.name().toLowerCase();
    }

    public static ApplicationEnvAttr of(String key) {
        if (key == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.key.equals(key))
                .findFirst()
                .orElse(null);
    }

}
