/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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
package cn.orionsec.ops.handler.tail;

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.constant.tail.FileTailMode;
import cn.orionsec.ops.entity.dto.file.FileTailDTO;
import cn.orionsec.ops.utils.WebSockets;
import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

/**
 * 文件tail处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/11 23:48
 */
@Component
@Slf4j
public class TailFileHandler implements WebSocketHandler {

    @Resource
    private TailSessionHolder tailSessionHolder;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String id = session.getId();
        FileTailDTO config = (FileTailDTO) session.getAttributes().get(WebSockets.CONFIG);
        String token = (String) session.getAttributes().get(WebSockets.TOKEN);
        log.info("tail 建立ws连接 token: {}, id: {}, config: {}", token, id, JSON.toJSONString(config));
        try {
            this.openTailHandler(session, config, token);
        } catch (Exception e) {
            log.error("tail 打开处理器-失败 id: {}", id, e);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("tail 操作异常拦截 token: {}, id: {}", WebSockets.getToken(session), session.getId(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String token = (String) session.getAttributes().get(WebSockets.TOKEN);
        // 释放资源
        ITailHandler handler = tailSessionHolder.removeSession(token);
        if (handler == null) {
            return;
        }
        handler.close();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 打开文件处理器
     *
     * @param session  session
     * @param fileTail tailDTO
     * @param token    token
     * @throws Exception Exception
     */
    private void openTailHandler(WebSocketSession session, FileTailDTO fileTail, String token) throws Exception {
        TailFileHint hint = new TailFileHint();
        hint.setToken(token);
        hint.setMachineId(fileTail.getMachineId());
        hint.setPath(fileTail.getFilePath());
        hint.setOffset(fileTail.getOffset());
        hint.setCharset(fileTail.getCharset());
        hint.setCommand(fileTail.getCommand());
        FileTailMode mode = FileTailMode.of(fileTail.getMode());
        if (FileTailMode.TRACKER.equals(mode)) {
            // 获取 delay
            String delayValue = SystemEnvAttr.TRACKER_DELAY_TIME.getValue();
            int delay = Strings.isInteger(delayValue) ? Integer.parseInt(delayValue) : Const.TRACKER_DELAY_MS;
            hint.setDelay(Math.max(delay, Const.MIN_TRACKER_DELAY_MS));
        }
        log.info("tail 打开处理器-开始 token: {}, mode: {}, hint: {}", token, mode, JSON.toJSONString(hint));
        ITailHandler handler = ITailHandler.with(mode, hint, session);
        tailSessionHolder.addSession(token, handler);
        handler.start();
    }

}
