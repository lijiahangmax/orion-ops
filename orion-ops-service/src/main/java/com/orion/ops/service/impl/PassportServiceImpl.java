package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.service.api.PassportService;
import com.orion.utils.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 认证
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/16 23:34
 */
@Service("passportService")
public class PassportServiceImpl implements PassportService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public UserDTO getUserByToken(String token) {
        if (Strings.isBlank(token)) {
            return null;
        }
        String cache = redisTemplate.opsForValue().get(Strings.format(KeyConst.LOGIN_TOKEN_KEY, token));
        if (!Strings.isEmpty(cache)) {
            return JSON.parseObject(cache, UserDTO.class);
        }
        return null;
    }

}
