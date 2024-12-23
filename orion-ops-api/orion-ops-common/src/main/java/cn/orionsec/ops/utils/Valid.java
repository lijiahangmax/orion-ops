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
package cn.orionsec.ops.utils;

import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import cn.orionsec.kit.lang.utils.Arrays1;
import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.system.SystemEnvAttr;

import java.util.Collection;

/**
 * 参数合法化判断
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:45
 */
public class Valid extends cn.orionsec.kit.lang.utils.Valid {

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

    /**
     * 检查 api 调用是否成功
     *
     * @param wrapper wrapper
     * @param <T>     T
     * @return value
     */
    public static <T> T api(HttpWrapper<T> wrapper) {
        if (wrapper.isOk()) {
            return wrapper.getData();
        } else {
            throw Exceptions.httpRequest(null, String.valueOf(wrapper.getData()));
        }
    }

}
