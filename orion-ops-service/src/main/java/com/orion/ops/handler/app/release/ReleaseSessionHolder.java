package com.orion.ops.handler.app.release;

import com.orion.utils.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * release 实例持有者
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/31 9:55
 */
@Component
public class ReleaseSessionHolder {

    /**
     * session
     */
    private ConcurrentHashMap<Long, IReleaseProcessor> session = Maps.newCurrentHashMap();

    /**
     * 添加 session
     *
     * @param processor processor
     */
    public void addSession(IReleaseProcessor processor) {
        session.put(processor.getReleaseId(), processor);
    }

    /**
     * 获取 session
     *
     * @param id id
     * @return session
     */
    public IReleaseProcessor getSession(Long id) {
        return session.get(id);
    }

    /**
     * 移除 session
     *
     * @param id id
     */
    public void removeSession(Long id) {
        session.remove(id);
    }

}
