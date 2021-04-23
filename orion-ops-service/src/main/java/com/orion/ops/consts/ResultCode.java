package com.orion.ops.consts;

import com.orion.lang.wrapper.CodeInfo;

/**
 * 返回code
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 9:48
 */
public enum ResultCode implements CodeInfo {

    /**
     * 未认证
     */
    UNAUTHORIZED() {
        @Override
        public int code() {
            return 700;
        }

        @Override
        public String message() {
            return "未认证";
        }
    },

    /**
     * 无权限
     */
    NO_PERMISSION() {
        @Override
        public int code() {
            return 710;
        }

        @Override
        public String message() {
            return "无权限";
        }
    },

    /**
     * 登录失败
     */
    LOGIN_FAILURE() {
        @Override
        public int code() {
            return 500;
        }

        @Override
        public String message() {
            return "登录失败";
        }
    },

    ;

}
