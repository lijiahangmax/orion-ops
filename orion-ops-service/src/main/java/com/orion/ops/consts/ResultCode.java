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
            return MessageConst.UNAUTHORIZED;
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
            return MessageConst.NO_PERMISSION;
        }
    },

    /**
     * 文件未找到
     */
    FILE_MISSING() {
        @Override
        public int code() {
            return 720;
        }

        @Override
        public String message() {
            return MessageConst.FILE_ABSENT;
        }
    },

    ;

}
