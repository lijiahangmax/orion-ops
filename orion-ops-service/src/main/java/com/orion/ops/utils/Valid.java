package com.orion.ops.utils;

import com.orion.lang.utils.Arrays1;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.io.Files1;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.system.SystemEnvAttr;

import java.util.Collection;

/**
 * 参数合法化判断
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:45
 */
public class Valid extends com.orion.lang.utils.Valid {

    public static <T> T notNull(T object) {
        return notNull(object, MessageConst.ABSENT_PARAM);
    }

    public static String notBlank(String s) {
        return notBlank(s, MessageConst.ABSENT_PARAM);
    }

    public static <T extends Collection<?>> T notEmpty(T object) {
        return notEmpty(object, MessageConst.ABSENT_PARAM);
    }

    public static void eq(Object o1, Object o2) {
        eq(o1, o2, MessageConst.INVALID_PARAM);
    }

    public static void allNotNull(Object... objects) {
        if (objects != null) {
            for (Object t : objects) {
                notNull(t, MessageConst.ABSENT_PARAM);
            }
        }
    }

    public static void allNotBlank(String... ss) {
        if (ss != null) {
            for (String s : ss) {
                notBlank(s, MessageConst.ABSENT_PARAM);
            }
        }
    }

    public static boolean isTrue(boolean s) {
        return isTrue(s, MessageConst.INVALID_PARAM);
    }

    public static boolean isFalse(boolean s) {
        return isFalse(s, MessageConst.INVALID_PARAM);
    }

    public static <T extends Comparable<T>> T gte(T t1, T t2) {
        return gte(t1, t2, MessageConst.INVALID_PARAM);
    }

    @SafeVarargs
    public static <T> T in(T t, T... ts) {
        notNull(t);
        notEmpty(ts);
        isTrue(Arrays1.contains(ts, t), MessageConst.INVALID_PARAM);
        return t;
    }

    public static void isSafe(boolean s) {
        isTrue(s, MessageConst.UNSAFE_OPERATOR);
    }

    /**
     * 检查路径是否合法化 即不包含 ./ ../
     *
     * @param path path
     */
    public static void checkNormalize(String path) {
        Valid.notBlank(path);
        Valid.isTrue(Files1.isNormalize(path), MessageConst.PATH_NOT_NORMALIZE);
    }

    /**
     * 检查是否超过文件上传阈值
     *
     * @param size size
     */
    public static void checkUploadSize(Long size) {
        Valid.notNull(size);
        String uploadThreshold = SystemEnvAttr.SFTP_UPLOAD_THRESHOLD.getValue();
        if (size / Const.BUFFER_KB_1 / Const.BUFFER_KB_1 > Long.parseLong(uploadThreshold)) {
            throw Exceptions.argument(Strings.format(MessageConst.UPLOAD_TOO_LARGE, uploadThreshold, Files1.getSize(size)));
        }
    }

}
