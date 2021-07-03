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

    /**
     * 添加session
     *
     * @param id      id
     * @param session session
     */
    public void addSession(Long id, IExecHandler session) {
        HOLDER.put(id, session);
    }

    /**
     * 获取session
     *
     * @param id id
     * @return session
     */
    public IExecHandler getSession(Long id) {
        return HOLDER.get(id);
    }

    /**
     * 删除session
     *
     * @param id id
     * @return session
     */
    public IExecHandler removeSession(Long id) {
        return HOLDER.remove(id);
    }

}
