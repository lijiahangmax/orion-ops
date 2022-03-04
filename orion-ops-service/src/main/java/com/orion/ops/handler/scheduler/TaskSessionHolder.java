package com.orion.ops.handler.scheduler;

import com.orion.utils.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 任务会话持有者
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/24 21:05
 */
@Component
public class TaskSessionHolder {

    /**
     * key: recordId
     * value: ITaskProcessor
     */
    private final Map<Long, ITaskProcessor> holder = Maps.newCurrentHashMap();

    /**
     * 添加session
     *
     * @param id      id
     * @param session session
     */
    public void addSession(Long id, ITaskProcessor session) {
        holder.put(id, session);
    }

    /**
     * 获取session
     *
     * @param id id
     * @return session
     */
    public ITaskProcessor getSession(Long id) {
        return holder.get(id);
    }

    /**
     * 删除session
     *
     * @param id id
     * @return session
     */
    public ITaskProcessor removeSession(Long id) {
        return holder.remove(id);
    }

}
