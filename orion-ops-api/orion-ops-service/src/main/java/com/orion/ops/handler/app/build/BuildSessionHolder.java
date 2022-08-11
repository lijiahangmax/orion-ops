package com.orion.ops.handler.app.build;

import com.orion.lang.utils.collect.Maps;
import com.orion.ops.handler.app.machine.IMachineProcessor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * build 实例持有者
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/16 23:40
 */
@Component
public class BuildSessionHolder {

    /**
     * session
     */
    private final ConcurrentHashMap<Long, IMachineProcessor> session = Maps.newCurrentHashMap();

    /**
     * 添加 session
     *
     * @param processor processor
     */
    public void addSession(Long id, IMachineProcessor processor) {
        session.put(id, processor);
    }

    /**
     * 获取 session
     *
     * @param id id
     * @return session
     */
    public IMachineProcessor getSession(Long id) {
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
