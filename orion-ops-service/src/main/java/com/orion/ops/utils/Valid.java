package com.orion.ops.utils;

import com.orion.ops.consts.Const;

/**
 * 参数合法化判断
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:45
 */
public class Valid extends com.orion.utils.Valid {

    public static <T> T notNull(T object) {
        return notNull(object, Const.INVALID_PARAM);
    }

    public static String notBlank(String s) {
        return notBlank(s, Const.INVALID_PARAM);
    }

    public static void eq(Object o1, Object o2) {
        eq(o1, o2, Const.INVALID_PARAM);
    }

    public static void allNotNull(Object... objects) {
        if (objects != null) {
            for (Object t : objects) {
                notNull(t, Const.INVALID_PARAM);
            }
        }
    }

    public static void allNotBlank(String... ss) {
        if (ss != null) {
            for (String s : ss) {
                notBlank(s, Const.INVALID_PARAM);
            }
        }
    }

}
