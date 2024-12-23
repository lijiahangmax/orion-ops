/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.handler.terminal.manager;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.id.UUIds;
import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.Valid;
import cn.orionsec.kit.lang.utils.collect.Lists;
import cn.orionsec.kit.lang.utils.collect.Maps;
import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.kit.lang.utils.time.DateRanges;
import cn.orionsec.ops.constant.KeyConst;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.entity.config.TerminalConnectConfig;
import cn.orionsec.ops.entity.dto.terminal.TerminalWatcherDTO;
import cn.orionsec.ops.entity.request.machine.MachineTerminalManagerRequest;
import cn.orionsec.ops.entity.vo.machine.MachineTerminalManagerVO;
import cn.orionsec.ops.entity.vo.machine.TerminalWatcherVO;
import cn.orionsec.ops.handler.terminal.IOperateHandler;
import cn.orionsec.ops.utils.Currents;
import cn.orionsec.ops.utils.EventParamsHolder;
import com.alibaba.fastjson.JSON;
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
            TerminalConnectConfig hint = handler.getHint();
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
        String key = Strings.format(KeyConst.TERMINAL_WATCHER_TOKEN, watcherToken);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(cache),
                KeyConst.TERMINAL_WATCHER_TOKEN_EXPIRE, TimeUnit.SECONDS);
        // 设置返回
        TerminalConnectConfig hint = handler.getHint();
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
