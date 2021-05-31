package com.orion.ops.handler.terminal;

import com.orion.lang.collect.LimitList;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.entity.request.MachineTerminalManagerRequest;
import com.orion.ops.entity.vo.MachineTerminalManagerVO;
import com.orion.utils.Strings;
import com.orion.utils.time.DateRanges;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
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
     * value: id
     */
    protected Map<String, TerminalHandler> sessionStore = new ConcurrentHashMap<>();

    /**
     * session 列表
     *
     * @param request request
     * @return dataGrid
     */
    public DataGrid<MachineTerminalManagerVO> getOnlineTerminal(MachineTerminalManagerRequest request) {
        List<MachineTerminalManagerVO> sessionList = sessionStore.values()
                .stream()
                .filter(s -> Optional.ofNullable(request.getToken())
                        .filter(Strings::isNotBlank)
                        .map(t -> s.getToken().contains(t))
                        .orElse(true))
                .filter(s -> Optional.ofNullable(request.getHost())
                        .filter(Strings::isNotBlank)
                        .map(t -> s.getConfig().getMachineHost().contains(t))
                        .orElse(true))
                .filter(s -> Optional.ofNullable(request.getUserId())
                        .map(t -> s.getConfig().getUserId().equals(t))
                        .orElse(true))
                .filter(s -> Optional.ofNullable(request.getMachineId())
                        .map(t -> s.getConfig().getMachineId().equals(t))
                        .orElse(true))
                .filter(s -> {
                    if (request.getConnectedTimeStart() == null || request.getConnectedTimeEnd() == null) {
                        return true;
                    }
                    return DateRanges.inRange(request.getConnectedTimeStart(), request.getConnectedTimeEnd(), s.getConfig().getConnectedTime());
                })
                .map(s -> {
                    MachineTerminalManagerVO session = new MachineTerminalManagerVO();
                    session.setToken(s.getToken());
                    session.setUserId(s.getConfig().getUserId());
                    session.setConnectedTime(s.getConfig().getConnectedTime());
                    session.setMachineId(s.getConfig().getMachineId());
                    session.setHost(s.getConfig().getMachineHost());
                    session.setLogId(s.getConfig().getLogId());
                    return session;
                }).collect(Collectors.toList());
        List<MachineTerminalManagerVO> page = new LimitList<>(sessionList)
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
        TerminalHandler handler = sessionStore.get(token);
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

}
