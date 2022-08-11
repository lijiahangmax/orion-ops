package com.orion.ops.handler.terminal.watcher;

import com.orion.lang.utils.Threads;
import com.orion.lang.utils.collect.Maps;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.SchedulerPools;
import com.orion.ops.constant.ws.WsCloseCode;
import com.orion.ops.utils.WebSockets;
import lombok.SneakyThrows;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * terminal watcher 处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/29 11:34
 */
public class TerminalWatcherProcessor implements ITerminalWatcherProcessor, Runnable {

    private final Map<String, WebSocketSession> sessions;

    private final LinkedBlockingQueue<byte[]> queue;

    private volatile boolean run;

    public TerminalWatcherProcessor() {
        this.sessions = Maps.newCurrentHashMap();
        this.queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void watch() {
        this.run = true;
        Threads.start(this, SchedulerPools.TERMINAL_SCHEDULER);
    }

    @SneakyThrows
    @Override
    public void run() {
        while (run) {
            byte[] message = queue.poll(Const.MS_S_10, TimeUnit.MILLISECONDS);
            if (message == null || !run) {
                continue;
            }
            for (WebSocketSession session : sessions.values()) {
                WebSockets.sendText(session, message);
            }
        }
    }

    @Override
    public void sendMessage(byte[] message) {
        queue.add(message);
    }

    @Override
    public void addWatcher(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void removeWatcher(String id) {
        sessions.remove(id);
    }

    @Override
    public void close() {
        sessions.forEach((k, s) -> WebSockets.close(s, WsCloseCode.EOF));
        sessions.clear();
        queue.clear();
        this.run = false;
    }

}
