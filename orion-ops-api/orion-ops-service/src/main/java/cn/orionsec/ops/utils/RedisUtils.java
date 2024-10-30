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
package cn.orionsec.ops.utils;

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.KeyConst;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.io.Streams;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.HashSet;
import java.util.Set;

/**
 * redis 工具类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/6 11:09
 */
public class RedisUtils {

    private RedisUtils() {
    }

    /**
     * 扫描key
     *
     * @param redisTemplate redisTemplate
     * @param match         匹配值
     * @param count         数量
     * @return keys
     */
    public static Set<String> scanKeys(RedisTemplate<?, ?> redisTemplate, String match, int count) {
        return scanKeys(redisTemplate, ScanOptions.scanOptions()
                .match(match)
                .count(count)
                .build());
    }

    /**
     * 扫描key
     *
     * @param redisTemplate redisTemplate
     * @param scanOptions   scan
     * @return keys
     */
    public static Set<String> scanKeys(RedisTemplate<?, ?> redisTemplate, ScanOptions scanOptions) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keys = new HashSet<>();
            Cursor<byte[]> cursor = connection.scan(scanOptions);
            while (cursor.hasNext()) {
                keys.add(new String(cursor.next()));
            }
            Streams.close(cursor);
            return keys;
        });
    }

    /**
     * 删除用户登录&绑定 token
     *
     * @param userId userId
     */
    public static void deleteLoginToken(RedisTemplate<String, ?> redisTemplate, Long userId) {
        // 删除登录 token
        redisTemplate.delete(Strings.format(KeyConst.LOGIN_TOKEN_KEY, userId));
        // 删除绑定 token
        String scanMatches = Strings.format(KeyConst.LOGIN_TOKEN_BIND_KEY, userId, "*");
        Set<String> bindTokens = scanKeys(redisTemplate, scanMatches, Const.N_10000);
        if (!Lists.isEmpty(bindTokens)) {
            for (String bindToken : bindTokens) {
                redisTemplate.delete(bindToken);
            }
        }
    }

}
