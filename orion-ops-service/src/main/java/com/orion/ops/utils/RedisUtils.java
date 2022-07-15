package com.orion.ops.utils;

import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.io.Streams;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.KeyConst;
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
     * 删除用户登陆&绑定 token
     *
     * @param userId userId
     */
    public static void deleteLoginToken(RedisTemplate<String, ?> redisTemplate, Long userId) {
        // 删除登陆 token
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
