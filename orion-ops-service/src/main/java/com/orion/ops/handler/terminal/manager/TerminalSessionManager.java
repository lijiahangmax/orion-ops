package com.orion.ops.handler.terminal.manager;

import com.alibaba.fastjson.JSON;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.id.UUIds;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.Valid;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.time.DateRanges;
import com.orion.ops.constant.KeyConst;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.event.EventParamsHolder;
import com.orion.ops.entity.dto.TerminalWatcherDTO;
import com.orion.ops.entity.request.MachineTerminalManagerRequest;
import com.orion.ops.entity.vo.MachineTerminalManagerVO;
import com.orion.ops.entity.vo.TerminalWatcherVO;
import com.orion.ops.handler.terminal.IOperateHandler;
import com.orion.ops.handler.terminal.TerminalConnectHint;
import com.orion.ops.utils.Currents;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
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

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 会话 sessionId:handler
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
                .filter(s -> Optional.ofNullable(request.getMachineName())
                        .filter(Strings::isNotBlank)
                        .map(t -> s.getHint().getMachineName().toLowerCase().contains(t.toLowerCase()))
                        .orElse(true))
                .filter(s -> Optional.ofNullable(request.getMachineTag())
                        .filter(Strings::isNotBlank)
                        .map(t -> s.getHint().getMachineTag().contains(t))
                        .orElse(true))
                .filter(s -> Optional.ofNullable(request.getMachineHost())
                        .filter(Strings::isNotBlank)
                        .map(t -> s.getHint().getMachineHost().contains(t))
                        .orElse(true))
                .filter(s -> Optional.ofNullable(request.getUserId())
                        .map(t -> s.getHint().getUserId().equals(t))
                        .orElse(true))
                .filter(s -> Optional.ofNullable(request.getUsername())
                        .map(t -> s.getHint().getUsername().toLowerCase().contains(t.toLowerCase()))
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
                })
                .sorted(Comparator.comparing(MachineTerminalManagerVO::getConnectedTime).reversed())
                .collect(Collectors.toList());
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
    public void forceOffline(String token) {
        IOperateHandler handler = sessionHolder.get(token);
        Valid.notNull(handler, MessageConst.SESSION_PRESENT);
        try {
            // 下线
            handler.forcedOffline();
            // 设置日志参数
            TerminalConnectHint hint = handler.getHint();
            EventParamsHolder.addParam(EventKeys.TOKEN, token);
            EventParamsHolder.addParam(EventKeys.USERNAME, hint.getUsername());
            EventParamsHolder.addParam(EventKeys.NAME, hint.getMachineName());
        } catch (Exception e) {
            throw Exceptions.app(MessageConst.OPERATOR_ERROR, e);
        }
    }

    /**
     * 获取终端监视 token
     *
     * @param token    token
     * @param readonly readonly
     * @return watcher
     */
    public TerminalWatcherVO getWatcherToken(String token, Integer readonly) {
        IOperateHandler handler = sessionHolder.get(token);
        Valid.notNull(handler, MessageConst.SESSION_PRESENT);
        // 设置缓存
        String watcherToken = UUIds.random32();
        TerminalWatcherDTO cache = TerminalWatcherDTO.builder()
                .userId(Currents.getUserId())
                .token(token)
                .readonly(readonly)
                .build();
        String key = Strings.format(KeyConst.TERMINAL_WATCHER_TOKEN,watcherToken);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(cache),
                KeyConst.TERMINAL_WATCHER_TOKEN_EXPIRE, TimeUnit.SECONDS);
        // 设置返回
        TerminalConnectHint hint = handler.getHint();
        return TerminalWatcherVO.builder()
                .token(watcherToken)
                .readonly(readonly)
                .cols(hint.getCols())
                .rows(hint.getRows())
                .build();
    }

    public Map<String, IOperateHandler> getSessionHolder() {
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
