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
package com.orion.ops.utils;

import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.io.Streams;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

/**
 * 资源加载器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/16 17:58
 */
@Slf4j
public class ResourceLoader {

    /**
     * key: resource
     * value: content
     */
    private static final Map<String, String> RESOURCE = Maps.newMap();

    /**
     * 获取资源
     *
     * @param resource resource
     * @param loader   加载类
     * @return content
     */
    public static String get(String resource, Class<?> loader) {
        try (InputStream stream = loader.getResourceAsStream(resource)) {
            String content = Streams.toString(Objects.requireNonNull(stream));
            RESOURCE.put(resource, content);
            return content;
        } catch (IOException e) {
            throw Exceptions.notFound(Strings.format("resource not found {} by {}", resource, loader.getName()), e);
        }
    }

}
