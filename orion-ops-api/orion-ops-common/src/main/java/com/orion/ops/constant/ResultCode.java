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
    FILE_MISSING(720, MessageConst.FILE_ABSENT),

    /**
     * IP封禁
     */
    IP_BAN(730, MessageConst.IP_BAN),

    /**
     * 用户禁用
     */
    USER_DISABLE(740, MessageConst.USER_DISABLED),

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
