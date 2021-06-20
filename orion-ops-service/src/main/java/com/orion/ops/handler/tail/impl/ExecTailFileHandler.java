package com.orion.ops.handler.tail.impl;

import com.orion.ops.handler.tail.ITailHandler;
import com.orion.ops.handler.tail.TailFileHint;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

/**
 * tail -f 命令
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/20 18:44
 */
public class ExecTailFileHandler implements ITailHandler {

    @Getter
    private String token;

    private WebSocketSession session;

    private TailFileHint hint;

    public ExecTailFileHandler(String token, WebSocketSession session, TailFileHint hint) {
        this.token = token;
        this.session = session;
        this.hint = hint;
    }

    @Override
    public void start() {

    }

    @Override
    public void close() {

    }

    @Override
    public String toString() {
        return hint.getPath();
    }

}
