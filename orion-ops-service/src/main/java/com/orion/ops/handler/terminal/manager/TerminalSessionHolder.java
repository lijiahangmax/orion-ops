package com.orion.ops.handler.terminal.manager;

import com.orion.ops.handler.terminal.AbstractTerminalHandler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * terminal 会话持有者
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 22:39
 */
@Component
public class TerminalSessionHolder {

    /**
     * 已连接的 session
     * key: token
     * value: id
     */
    private final Map<String, AbstractTerminalHandler> SESSION_STORE = new ConcurrentHashMap<>();

    public Map<String, AbstractTerminalHandler> getSessionStore() {
        return SESSION_STORE;
    }

}
