package com.orion.ops.handler.tail;

import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.Strings;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.constant.tail.FileTailMode;
import com.orion.ops.entity.dto.FileTailDTO;
import com.orion.ops.utils.WebSockets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

import static com.orion.ops.utils.WebSockets.getToken;

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
        log.info("tail 建立ws连接 token: {}, id: {}, config: {}", getToken(session), id, JSON.toJSONString(config));
        try {
            this.openTailHandler(session, config);
        } catch (Exception e) {
            log.error("tail 打开处理器-失败 id: {}", id, e);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("tail 操作异常拦截 token: {}, id: {}", getToken(session), session.getId(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String token = session.getId();
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
     * @throws Exception Exception
     */
    private void openTailHandler(WebSocketSession session, FileTailDTO fileTail) throws Exception {
        String token = session.getId();
        TailFileHint hint = new TailFileHint();
        hint.setToken(token);
        hint.setMachineId(fileTail.getMachineId());
        hint.setPath(fileTail.getFilePath());
        hint.setOffset(fileTail.getOffset());
        hint.setCharset(fileTail.getCharset());
        hint.setCommand(fileTail.getCommand());
        FileTailMode mode = FileTailMode.of(fileTail.getMode());
        if (FileTailMode.TRACKER.equals(mode)) {
            // 获取delay
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
