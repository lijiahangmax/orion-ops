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
package com.orion.ops.constant;

import com.orion.lang.define.wrapper.CodeInfo;

/**
 * wrapper 返回 code
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 9:48
 */
public enum ResultCode implements CodeInfo {

    /**
     * 未认证
     */
    UNAUTHORIZED(700, MessageConst.UNAUTHORIZED),

    /**
     * 无权限
     */
    NO_PERMISSION(710, MessageConst.NO_PERMISSION),

    /**
     * 文件未找到
     */
    FILE_MISSING(720, MessageConst.FILE_MISSING),

    /**
     * IP封禁
     */
    IP_BAN(730, MessageConst.IP_BAN),

    /**
     * 用户禁用
     */
    USER_DISABLED(740, MessageConst.USER_DISABLED),

    /**
     * 非法访问
     */
    ILLEGAL_ACCESS(750, MessageConst.ILLEGAL_ACCESS),

    /**
     * 演示模式不支持此功能
     */
    DEMO_DISABLE_API(760, MessageConst.DEMO_DISABLE_API),

    ;

    private final int code;

    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

}
