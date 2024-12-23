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
package cn.orionsec.ops.constant.event;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import cn.orionsec.kit.lang.utils.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.LinkedHashMap;

/**
 * 操作类型测试
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/7 17:25
 */
public class EventTypeTest {

    @Test
    public void generatorEnumJson() {
        EventType[] values = EventType.values();
        LinkedHashMap<String, EventTypeTest.EventTypeJson> map = Maps.newLinkedMap();
        for (EventType value : values) {
            map.put(value.name(), new EventTypeTest.EventTypeJson(value.getType(), value.getLabel(), value.getClassify().getClassify()));
        }
        System.out.println(JSONArray.toJSONString(map, SerializerFeature.PrettyFormat, SerializerFeature.MapSortField));
    }

    @Data
    @AllArgsConstructor
    static class EventTypeJson {

        @JSONField(ordinal = 0)
        private Integer value;

        @JSONField(ordinal = 1)
        private String label;

        @JSONField(ordinal = 2)
        private Integer classify;

    }

}
