package com.orion.ops.utils;

import com.orion.ops.consts.MessageConst;
import com.orion.utils.Exceptions;

import java.util.Collection;

/**
 * 参数合法化判断
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:45
 */
public class Valid extends com.orion.utils.Valid {

    public static <T> T notNull(T object) {
        return notNull(object, MessageConst.MISSING_PARAM);
    }

    public static String notBlank(String s) {
        return notBlank(s, MessageConst.MISSING_PARAM);
    }

    public static <T extends Collection<?>> T notEmpty(T object) {
        return notEmpty(object, MessageConst.MISSING_PARAM);
    }

    public static void eq(Object o1, Object o2) {
        eq(o1, o2, MessageConst.INVALID_PARAM);
    }

    public static void allNotNull(Object... objects) {
        if (objects != null) {
            for (Object t : objects) {
                notNull(t, MessageConst.MISSING_PARAM);
            }
        }
    }

    public static void allNotBlank(String... ss) {
        if (ss != null) {
            for (String s : ss) {
                notBlank(s, MessageConst.MISSING_PARAM);
            }
        }
    }

    public static boolean isTrue(boolean s) {
        return isTrue(s, MessageConst.INVALID_PARAM);
    }

    public static boolean isFalse(boolean s) {
        return isFalse(s, MessageConst.INVALID_PARAM);
    }

    public static void sftp(boolean s) {
        if (!s) {
            throw Exceptions.sftp(MessageConst.SFTP_OPERATOR_ERROR);
        }
    }

    public static <T extends Comparable<T>> T gte(T t1, T t2) {
        return gte(t1, t2, MessageConst.INVALID_PARAM);
    }

}
