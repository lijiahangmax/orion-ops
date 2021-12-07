package com.orion.ops.handler.tail;

import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Maps;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final Map<String, ITailHandler> holder = Maps.newCurrentHashMap();

    /**
     * key: machineId:filePath
     * value: token
     */
    private final Map<String, List<String>> fileTokenMapping = Maps.newCurrentHashMap();

    /**
     * 添加session
     *
     * @param token   token
     * @param session session
     */
    public void addSession(String token, ITailHandler session) {
        holder.put(token, session);
        fileTokenMapping.computeIfAbsent(session.getMachineId() + ":" + session.getFilePath(), s -> Lists.newList()).add(token);
    }

    /**
     * 获取session
     *
     * @param token token
     * @return session
     */
    public ITailHandler getSession(String token) {
        return holder.get(token);
    }

    /**
     * 获取session
     *
     * @param machineId machineId
     * @param path      path
     * @return session
     */
    public List<ITailHandler> getSession(Long machineId, String path) {
        List<String> tokenList = fileTokenMapping.get(machineId + ":" + path);
        if (Lists.isEmpty(tokenList)) {
            return Lists.empty();
        }
        return tokenList.stream()
                .map(holder::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 删除session
     *
     * @param token token
     * @return session
     */
    public ITailHandler removeSession(String token) {
        ITailHandler handler = holder.remove(token);
        if (handler != null) {
            fileTokenMapping.remove(handler.getMachineId() + ":" + handler.getFilePath());
        }
        return handler;
    }

}
