package com.orion.ops.handler.tail;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * tail 持有者
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

    public Map<String, ITailHandler> getHolder() {
        return HOLDER;
    }

}
