package com.orion.ops.constant.event;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.orion.lang.utils.collect.Maps;
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
