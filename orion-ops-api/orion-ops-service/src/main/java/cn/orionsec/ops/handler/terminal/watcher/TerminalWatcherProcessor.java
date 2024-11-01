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
package cn.orionsec.ops.handler.terminal.watcher;

import cn.orionsec.kit.lang.utils.Threads;
import cn.orionsec.kit.lang.utils.collect.Maps;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.SchedulerPools;
import cn.orionsec.ops.constant.ws.WsCloseCode;
import cn.orionsec.ops.utils.WebSockets;
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
        Threads.start(this, SchedulerPools.TERMINAL_WATCHER_SCHEDULER);
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
