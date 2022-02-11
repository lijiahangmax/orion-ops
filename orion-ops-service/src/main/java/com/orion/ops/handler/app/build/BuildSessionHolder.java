package com.orion.ops.handler.app.build;

import com.orion.utils.collect.Maps;
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
    private ConcurrentHashMap<Long, IBuilderProcessor> session = Maps.newCurrentHashMap();

    /**
     * 添加 session
     *
     * @param processor processor
     */
    public void addSession(IBuilderProcessor processor) {
        session.put(processor.getBuildId(), processor);
    }

    /**
     * 获取 session
     *
     * @param id id
     * @return session
     */
    public IBuilderProcessor getSession(Long id) {
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
