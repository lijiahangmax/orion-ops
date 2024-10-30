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
package cn.orionsec.ops.interceptor;

import cn.orionsec.ops.constant.KeyConst;
import cn.orionsec.ops.entity.dto.file.FileTailDTO;
import cn.orionsec.ops.utils.WebSockets;
import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import java.util.Map;

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
        String token = WebSockets.getToken(request);
        String tokenKey = Strings.format(KeyConst.FILE_TAIL_ACCESS_TOKEN, token);
        String tokenValue = redisTemplate.opsForValue().get(tokenKey);
        boolean access = false;
        if (!Strings.isBlank(tokenValue)) {
            // 设置信息
            access = true;
            attributes.put(WebSockets.CONFIG, JSON.parseObject(tokenValue, FileTailDTO.class));
            attributes.put(WebSockets.TOKEN, token);
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
