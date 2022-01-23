package com.orion.ops.utils;

import com.orion.ops.consts.event.EventKeys;

import java.util.Map;

/**
 * 事件日志工具
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/22 21:05
 */
public class EventLogUtils {

    private EventLogUtils() {
    }

    /**
     * 移除内部key
     *
     * @param map map
     */
    public static void removeInnerKeys(Map<String, ?> map) {
        map.remove(EventKeys.INNER_USER_ID);
        map.remove(EventKeys.INNER_USER_NAME);
        map.remove(EventKeys.INNER_SAVE);
        map.remove(EventKeys.INNER_TEMPLATE);
    }

}
