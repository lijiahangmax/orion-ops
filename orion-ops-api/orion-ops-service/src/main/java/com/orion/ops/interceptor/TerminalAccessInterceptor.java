package com.orion.ops.interceptor;

import com.orion.lang.utils.Strings;
import com.orion.ops.constant.KeyConst;
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
 * terminal 访问拦截器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/14 0:31
 */
@Component
@Slf4j
public class TerminalAccessInterceptor implements HandshakeInterceptor {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // 获取 token
        String token = getToken(request);
        String tokenKey = Strings.format(KeyConst.TERMINAL_ACCESS_TOKEN, token);
        String tokenValue = redisTemplate.opsForValue().get(tokenKey);
        boolean access = false;
        if (!Strings.isBlank(tokenValue)) {
            // 设置用户机器信息
            access = true;
            String[] pair = tokenValue.split("_");
            attributes.put(WebSockets.UID, Long.valueOf(pair[0]));
            attributes.put(WebSockets.MID, Long.valueOf(pair[1]));
            // 删除 token
            redisTemplate.delete(tokenKey);
        }
        log.info("terminal尝试打开ws连接开始 token: {}, 结果: {}", token, access);
        return access;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

}
