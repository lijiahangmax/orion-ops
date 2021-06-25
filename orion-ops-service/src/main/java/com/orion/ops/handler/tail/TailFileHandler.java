package com.orion.ops.handler.tail;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.tail.FileTailMode;
import com.orion.ops.consts.ws.WsCloseCode;
import com.orion.ops.entity.dto.FileTailDTO;
import com.orion.ops.handler.tail.impl.ExecTailFileHandler;
import com.orion.ops.handler.tail.impl.TrackerTailFileHandler;
import com.orion.utils.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private TailSessionHolder tailSessionHolder;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String id = session.getId();
        String token = getToken(session);
        // 检查token
        String tokenKey = Strings.format(KeyConst.FILE_TAIL_ACCESS, token);
        String tokenValue = redisTemplate.opsForValue().get(tokenKey);
        FileTailDTO fileTail = Optional.ofNullable(tokenValue)
                .map(t -> JSON.parseObject(tokenValue, FileTailDTO.class))
                .orElse(null);
        // 未查询到token
        if (fileTail == null) {
            session.close(WsCloseCode.INCORRECT_TOKEN.close());
            return;
        }
        // 检查bind
        String bindKey = Strings.format(KeyConst.FILE_TAIL_BIND, token);
        String bindValue = redisTemplate.opsForValue().get(bindKey);
        if (bindValue != null) {
            session.close(WsCloseCode.TOKEN_BIND.close());
            return;
        }
        // 删除token
        redisTemplate.delete(tokenKey);
        // token绑定
        redisTemplate.opsForValue().set(bindKey, id, KeyConst.TERMINAL_BIND_EXPIRE, TimeUnit.SECONDS);
        // 刷新token 过期时间
        redisTemplate.expire(tokenKey, KeyConst.TERMINAL_ACCESS_TOKEN_EXPIRE, TimeUnit.SECONDS);
        log.info("tail 建立ws连接 token: {}, id: {}", token, id);
        // 打开日志流
        try {
            this.openTailHandler(session, token, fileTail);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("tail 打开处理器-失败 e: {}, token: {}, id: {}", e, token, id);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("tail 操作异常拦截 token: {}, id: {}, e: {}", getToken(session), session.getId(), exception);
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String id = session.getId();
        String token = getToken(session);
        String bindKey = Strings.format(KeyConst.FILE_TAIL_BIND, token);
        String bindValue = redisTemplate.opsForValue().get(bindKey);
        if (!id.equals(bindValue)) {
            return;
        }
        log.info("tail 关闭连接 token: {}, id: {}, code: {}, reason: {}", token, id, status.getCode(), status.getReason());
        redisTemplate.delete(bindKey);
        // 释放资源
        ITailHandler handler = tailSessionHolder.getHolder().remove(token);
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
     * @param token    token
     * @param fileTail tailDTO
     * @throws Exception Exception
     */
    private void openTailHandler(WebSocketSession session, String token, FileTailDTO fileTail) throws Exception {
        String id = session.getId();
        TailFileHint hint = new TailFileHint();
        hint.setSessionId(id);
        hint.setMachineId(fileTail.getMachineId());
        hint.setPath(fileTail.getFilePath());
        hint.setOffset(fileTail.getOffset());
        hint.setCharset(fileTail.getCharset());
        FileTailMode mode = FileTailMode.of(fileTail.getMode());
        log.info("tail 打开处理器-开始 token: {}, id: {}, mode: {}, hint: {}", token, id, mode, JSON.toJSONString(hint));
        ITailHandler handler;
        if (FileTailMode.TRACKER.equals(mode)) {
            handler = new TrackerTailFileHandler(token, session, hint);
        } else if (FileTailMode.TAIL.equals(mode)) {
            handler = new ExecTailFileHandler(token, session, hint);
        } else {
            return;
        }
        tailSessionHolder.getHolder().put(token, handler);
        handler.start();
    }

}
