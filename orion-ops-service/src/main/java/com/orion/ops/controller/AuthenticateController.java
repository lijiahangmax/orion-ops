package com.orion.ops.controller;

import com.alibaba.fastjson.JSON;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.lang.wrapper.Wrapper;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.utils.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 认证controller
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/1 17:05
 */
@RestController
@RequestMapping("/orion/auth")
public class AuthenticateController {

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    @RequestMapping("/login")
    public Wrapper<?> test1() {
        UserDTO user = new UserDTO();
        user.setId(10000L);
        redisTemplate.opsForValue().set(Strings.format(KeyConst.LOGIN_TOKEN_KEY, "1u"), JSON.toJSONString(user));
        return HttpWrapper.ok();
    }

}
