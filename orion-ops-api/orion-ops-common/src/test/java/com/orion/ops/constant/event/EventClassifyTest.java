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
 * 操作分类测试
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/7 15:58
 */
public class EventClassifyTest {

    @Test
    public void generatorEnumJson() {
        EventClassify[] values = EventClassify.values();
        LinkedHashMap<String, EventClassifyJson> map = Maps.newLinkedMap();
        for (EventClassify value : values) {
            map.put(value.name(), new EventClassifyJson(value.getClassify(), value.getLabel()));
        }
        System.out.println(JSONArray.toJSONString(map, SerializerFeature.PrettyFormat, SerializerFeature.MapSortField));
    }

    @Data
    @AllArgsConstructor
    static class EventClassifyJson {

        @JSONField(ordinal = 0)
        private Integer value;

        @JSONField(ordinal = 1)
        private String label;

    }

}
