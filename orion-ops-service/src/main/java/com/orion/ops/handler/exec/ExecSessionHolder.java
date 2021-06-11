package com.orion.ops.handler.exec;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * exec 实例持有者
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 23:13
 */
@Component
public class ExecSessionHolder {

    /**
     * key: execId
     * value: IExecHandler
     */
    private final Map<Long, IExecHandler> HOLDER = new ConcurrentHashMap<>();

    public Map<Long, IExecHandler> getHolder() {
        return HOLDER;
    }

}
