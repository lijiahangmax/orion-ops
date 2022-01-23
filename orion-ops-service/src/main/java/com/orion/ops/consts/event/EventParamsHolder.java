package com.orion.ops.consts.event;

import com.orion.lang.collect.MutableMap;
import com.orion.utils.collect.Maps;

/**
 * 操作日志参数信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/23 17:24
 */
public class EventParamsHolder {

    /**
     * 参数
     */
    private static final ThreadLocal<MutableMap<String, Object>> PARAMS = ThreadLocal.withInitial(Maps::newMutableLinkedMap);

    public static MutableMap<String, Object> get() {
        return PARAMS.get();
    }

    public static void set(MutableMap<String, Object> user) {
        PARAMS.set(user);
    }

    public static void remove() {
        PARAMS.remove();
    }

    /**
     * 设置参数
     *
     * @param key   key
     * @param value value
     */
    public static void addParam(String key, Object value) {
        PARAMS.get().put(key, value);
    }

    /**
     * 设置是否保存
     *
     * @param save 是否保存
     */
    public static void setSave(boolean save) {
        PARAMS.get().put(EventKeys.INNER_SAVE, save);
    }

}
