package com.orion.ops.interceptor;

import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.Strings;
import com.orion.ops.constant.KeyConst;
import com.orion.ops.entity.dto.FileTailDTO;
import com.orion.ops.utils.WebSockets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import java.util.Map;

import static com.orion.ops.utils.WebSockets.getToken;

/**
 * tail 文件拦截器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/16 14:52
 */
@Component
@Slf4j
public class TailFileInterceptor implements HandshakeInterceptor {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = getToken(request);
        String tokenKey = Strings.format(KeyConst.FILE_TAIL_ACCESS_TOKEN, token);
        String tokenValue = redisTemplate.opsForValue().get(tokenKey);
        boolean access = false;
        if (!Strings.isBlank(tokenValue)) {
            // 设置信息
            access = true;
            attributes.put(WebSockets.CONFIG, JSON.parseObject(tokenValue, FileTailDTO.class));
            // 删除 token
            redisTemplate.delete(tokenKey);
        }
        log.info("tail 尝试建立ws连接开始 token: {}, 结果: {}", token, access);
        return access;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

}
