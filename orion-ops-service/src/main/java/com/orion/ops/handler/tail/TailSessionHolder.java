package com.orion.ops.handler.tail;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * tail 会话持有者
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/18 17:34
 */
@Component
public class TailSessionHolder {

    /**
     * key: token
     * value: ITailHandler
     */
    private final Map<String, ITailHandler> HOLDER = new ConcurrentHashMap<>();

    /**
     * 添加session
     *
     * @param token   token
     * @param session session
     */
    public void addSession(String token, ITailHandler session) {
        HOLDER.put(token, session);
    }

    /**
     * 获取session
     *
     * @param token token
     * @return session
     */
    public ITailHandler getSession(String token) {
        return HOLDER.get(token);
    }

    /**
     * 删除session
     *
     * @param token token
     * @return session
     */
    public ITailHandler removeSession(String token) {
        return HOLDER.remove(token);
    }

}
