package com.orion.ops.interceptor;

import com.orion.ops.consts.KeyConst;
import com.orion.utils.Booleans;
import com.orion.utils.Strings;
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
        String tokenKey = Strings.format(KeyConst.FILE_TAIL_ACCESS, token);
        boolean access = Booleans.isTrue(redisTemplate.hasKey(tokenKey));
        if (access) {
            String bindKey = Strings.format(KeyConst.FILE_TAIL_BIND, token);
            access = !Booleans.isTrue(redisTemplate.hasKey(bindKey));
        }
        log.info("tail 尝试建立ws连接开始 token: {}, 结果: {}", token, access);
        return access;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

}
