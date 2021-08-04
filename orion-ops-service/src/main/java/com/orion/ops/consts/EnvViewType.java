package com.orion.ops.consts;

import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.ops.utils.AttrConverts;
import lombok.AllArgsConstructor;

import java.util.Map;

/**
 * 环境变量查看类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/3 18:53
 */
@AllArgsConstructor
public enum EnvViewType {

    /**
     * xml
     */
    XML(10) {
        @Override
        public String toValue(Map<String, String> value) {
            return AttrConverts.toXml(value);
        }

        @Override
        public MutableLinkedHashMap<String, String> toMap(String value) {
            return AttrConverts.fromXml(value);
        }
    },

    /**
     * json
     */
    JSON(20) {
        @Override
        public String toValue(Map<String, String> value) {
            return AttrConverts.toJson(value);
        }

        @Override
        public MutableLinkedHashMap<String, String> toMap(String value) {
            return AttrConverts.fromJson(value);
        }
    },

    /**
     * properties
     */
    PROPERTIES(30) {
        @Override
        public String toValue(Map<String, String> value) {
            return AttrConverts.toProperties(value);
        }

        @Override
        public MutableLinkedHashMap<String, String> toMap(String value) {
            return AttrConverts.fromProperties(value);
        }
    },

    ;

    private final Integer type;

    public abstract String toValue(Map<String, String> value);

    public abstract MutableLinkedHashMap<String, String> toMap(String value);

    public static EnvViewType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (EnvViewType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

}
