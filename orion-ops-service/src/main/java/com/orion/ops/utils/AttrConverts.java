package com.orion.ops.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.orion.lang.collect.MutableArrayList;
import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.ops.consts.Const;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Maps;
import com.orion.utils.ext.dom.*;
import org.dom4j.io.OutputFormat;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

/**
 * 属性转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/2 11:40
 */
public class AttrConverts {

    private static final String XML_ROOT_TAG = "root";

    private static final String XML_NODE_TAG = "env";

    private static final String XML_NODE_KEY_ATTR = "key";

    private AttrConverts() {
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
     * env -> xml
     *
     * @param attrs env
     * @return xml
     */
    public static String toXml(Map<String, String> attrs) {
        DomBuilder builder = DomBuilder.create();
        DomElement root = builder.createRootElement(XML_ROOT_TAG);
        attrs.forEach((k, v) -> {
            DomElement child = new DomElement(XML_NODE_TAG, v);
            child.addAttributes(XML_NODE_KEY_ATTR, k);
            if (DomSupport.isEscape(v)) {
                child.cdata();
            }
            root.addChildNode(child);
        });
        builder.build();
        OutputFormat format = new OutputFormat(Const.SPACE_4, true);
        format.setExpandEmptyElements(true);
        // 隐藏头
        format.setSuppressDeclaration(true);
        return DomSupport.format(builder.getDocument(), format).substring(1);
    }

    /**
     * env -> yml
     *
     * @param attrs env
     * @return yml
     */
    public static String toYml(Map<String, String> attrs) {
        return new Yaml().dumpAsMap(attrs);
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
     * json -> env
     *
     * @param json xml
     * @return json
     */
    public static MutableLinkedHashMap<String, String> fromJson(String json) {
        MutableLinkedHashMap<String, String> map = Maps.newMutableLinkedMap();
        JSONObject res = JSON.parseObject(json, Feature.OrderedField);
        res.forEach((k, v) -> map.put(k, v + Strings.EMPTY));
        return map;
    }

    /**
     * xml -> env
     *
     * @param xml xml
     * @return env
     */
    public static MutableLinkedHashMap<String, String> fromXml(String xml) {
        MutableLinkedHashMap<String, String> map = Maps.newMutableLinkedMap();
        DomNode domNode = DomExt.of(xml).toDomNode().get(XML_NODE_TAG);
        MutableArrayList<DomNode> list;
        if (domNode.getValueClass() == String.class) {
            list = Lists.newMutableList();
            list.add(domNode);
        } else {
            list = domNode.getListValue();
        }
        list.forEach(e -> {
            Map<String, String> attr = e.getAttr();
            if (Maps.isEmpty(attr)) {
                return;
            }
            String key = attr.get(XML_NODE_KEY_ATTR);
            if (Strings.isBlank(key)) {
                return;
            }
            map.put(key, e.getStringValue());
        });
        return map;
    }

    /**
     * yml -> env
     *
     * @param yml yml
     * @return env
     */
    @SuppressWarnings("unchecked")
    public static MutableLinkedHashMap<String, String> fromYml(String yml) {
        return new Yaml().loadAs(yml, MutableLinkedHashMap.class);
    }

    /**
     * properties -> env
     *
     * @param properties properties
     * @return env
     */
    public static MutableLinkedHashMap<String, String> fromProperties(String properties) {
        MutableLinkedHashMap<String, String> map = Maps.newMutableLinkedMap();
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
