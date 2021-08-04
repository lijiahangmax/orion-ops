package com.orion.ops.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.ops.consts.Const;
import com.orion.utils.Strings;
import com.orion.utils.collect.Maps;
import com.orion.utils.ext.dom.DomBuilder;
import com.orion.utils.ext.dom.DomElement;
import com.orion.utils.ext.dom.DomExt;
import com.orion.utils.ext.dom.DomSupport;

import java.util.Map;

/**
 * 属性转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/2 11:40
 */
public class AttrConverts {

    private AttrConverts() {
    }

    /**
     * env -> xml
     *
     * @param attrs env
     * @return xml
     */
    public static String toXml(Map<String, String> attrs) {
        DomBuilder builder = DomBuilder.create();
        DomElement root = builder.createRootElement("env");
        attrs.forEach((k, v) -> {
            DomElement child = new DomElement(k, v);
            if (DomSupport.isEscape(v)) {
                child.cdata();
            }
            root.addChildNode(child);
        });
        return builder.build().getFormatXml();
    }

    /**
     * env -> json
     *
     * @param attrs env
     * @return json
     */
    public static String toJson(Map<String, String> attrs) {
        return JSON.toJSONString(attrs, SerializerFeature.PrettyFormat);
    }

    /**
     * env -> properties
     *
     * @param attrs env
     * @return properties
     */
    public static String toProperties(Map<String, String> attrs) {
        StringBuilder sb = new StringBuilder();
        attrs.forEach((k, v) -> {
            sb.append(k).append("=").append(v).append(Const.LF);
        });
        return sb.toString();
    }

    /**
     * xml -> env
     *
     * @param xml xml
     * @return env
     */
    public static MutableLinkedHashMap<String, String> fromXml(String xml) {
        MutableLinkedHashMap<String, String> map = Maps.newLinkedMutableMap();
        DomExt.of(xml).toDomNode().forEach((k, v) -> {
            map.put(k, v.getStringValue());
        });
        return map;
    }

    /**
     * json -> env
     *
     * @param json xml
     * @return json
     */
    public static MutableLinkedHashMap<String, String> fromJson(String json) {
        MutableLinkedHashMap<String, String> map = Maps.newLinkedMutableMap();
        JSONObject res = JSON.parseObject(json, Feature.OrderedField);
        res.forEach((k, v) -> map.put(k, v + Strings.EMPTY));
        return map;
    }

    /**
     * properties -> env
     *
     * @param properties properties
     * @return env
     */
    public static MutableLinkedHashMap<String, String> fromProperties(String properties) {
        MutableLinkedHashMap<String, String> map = Maps.newLinkedMutableMap();
        String[] rows = properties.split(Const.LF);
        for (String row : rows) {
            String key = row.split("=")[0];
            String value;
            if (row.length() > key.length()) {
                value = row.substring(key.length() + 1);
            } else {
                value = Strings.EMPTY;
            }
            map.put(key, value);
        }
        return map;
    }

}
