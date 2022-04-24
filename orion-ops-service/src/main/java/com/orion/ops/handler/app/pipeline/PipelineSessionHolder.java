package com.orion.ops.handler.app.pipeline;

import com.orion.utils.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 流水线会话
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/15 20:00
 */
@Component
public class PipelineSessionHolder {

    /**
     * session
     */
    private ConcurrentHashMap<Long, IPipelineProcessor> session = Maps.newCurrentHashMap();

    /**
     * 添加 session
     *
     * @param processor processor
     */
    public void addSession(IPipelineProcessor processor) {
        session.put(processor.getTaskId(), processor);
    }

    /**
     * 获取 session
     *
     * @param id id
     * @return session
     */
    public IPipelineProcessor getSession(Long id) {
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
