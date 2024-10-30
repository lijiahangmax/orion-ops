/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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
package cn.orionsec.ops.constant.env;

import cn.orionsec.ops.utils.AttrConverts;
import com.orion.lang.define.collect.MutableLinkedHashMap;
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
     * json
     */
    JSON(10) {
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
     * xml
     */
    XML(20) {
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
     * yml
     */
    YML(30) {
        @Override
        public String toValue(Map<String, String> value) {
            return AttrConverts.toYml(value);
        }

        @Override
        public MutableLinkedHashMap<String, String> toMap(String value) {
            return AttrConverts.fromYml(value);
        }
    },

    /**
     * properties
     */
    PROPERTIES(40) {
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
