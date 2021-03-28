package com.orion.ops;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
class RedisTests {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void testConn() {
        redisTemplate.opsForValue().set("key", "val");
        System.out.println(redisTemplate.opsForValue().get("key"));
    }

}
