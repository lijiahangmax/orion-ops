package com.orion.ops.constant.alarm;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 报警组通知方式
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/25 15:56
 */
@Getter
@AllArgsConstructor
public enum AlarmGroupNotifyType {

    /**
     * webhook 通知
     */
    WEBHOOK(10),

    ;

    private final Integer type;

    public static AlarmGroupNotifyType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (AlarmGroupNotifyType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
