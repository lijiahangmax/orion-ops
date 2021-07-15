package com.orion.ops.handler.terminal.manager;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.entity.request.MachineTerminalManagerRequest;
import com.orion.ops.entity.vo.MachineTerminalManagerVO;
import com.orion.ops.handler.terminal.IOperateHandler;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Maps;
import com.orion.utils.convert.Converts;
import com.orion.utils.time.DateRanges;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 终端管理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 22:46
 */
@Component
public class TerminalSessionManager {

    /**
     * 已连接的 session
     * key: token
     * value: handler
     */
    private final Map<String, IOperateHandler> sessionHolder = Maps.newCurrentHashMap();

    /**
     * session 列表
     *
     * @param request request
     * @return dataGrid
     */
    public DataGrid<MachineTerminalManagerVO> getOnlineTerminal(MachineTerminalManagerRequest request) {
        List<MachineTerminalManagerVO> sessionList = sessionHolder.values()
                .stream()
                .filter(s -> Optional.ofNullable(request.getToken())
                        .filter(Strings::isNotBlank)
                        .map(t -> s.getToken().contains(t))
                        .orElse(true))
                .filter(s -> Optional.ofNullable(request.getHost())
                        .filter(Strings::isNotBlank)
                        .map(t -> s.getHint().getMachineHost().contains(t))
                        .orElse(true))
                .filter(s -> Optional.ofNullable(request.getUserId())
                        .map(t -> s.getHint().getUserId().equals(t))
                        .orElse(true))
                .filter(s -> Optional.ofNullable(request.getMachineId())
                        .map(t -> s.getHint().getMachineId().equals(t))
                        .orElse(true))
                .filter(s -> {
                    if (request.getConnectedTimeStart() == null || request.getConnectedTimeEnd() == null) {
                        return true;
                    }
                    return DateRanges.inRange(request.getConnectedTimeStart(), request.getConnectedTimeEnd(), s.getHint().getConnectedTime());
                })
                .map(s -> {
                    MachineTerminalManagerVO vo = Converts.to(s.getHint(), MachineTerminalManagerVO.class);
                    vo.setToken(s.getToken());
                    return vo;
                }).collect(Collectors.toList());
        List<MachineTerminalManagerVO> page = Lists.newLimitList(sessionList)
                .limit(request.getLimit())
                .page(request.getPage());
        return DataGrid.of(page, sessionList.size());
    }

    /**
     * 强制下线
     *
     * @param token token
     */
    public HttpWrapper<?> forceOffline(String token) {
        IOperateHandler handler = sessionHolder.get(token);
        if (handler == null) {
            return HttpWrapper.error("未查询到连接信息");
        }
        try {
            handler.forcedOffline();
            return HttpWrapper.ok();
        } catch (Exception e) {
            return HttpWrapper.error("下线失败");
        }
    }

    public Map<String, IOperateHandler> getSessionStore() {
        return sessionHolder;
    }

    public IOperateHandler getSession(String key) {
        return sessionHolder.get(key);
    }

    public IOperateHandler removeSession(String key) {
        return sessionHolder.remove(key);
    }

    public void addSession(String key, IOperateHandler handler) {
        sessionHolder.put(key, handler);
    }

}
