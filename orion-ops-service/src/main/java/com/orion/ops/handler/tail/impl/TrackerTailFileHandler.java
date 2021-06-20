package com.orion.ops.handler.tail.impl;

import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.handler.tail.ITailHandler;
import com.orion.ops.handler.tail.TailFileHint;
import com.orion.tail.Tracker;
import com.orion.tail.delay.DelayTracker;
import com.orion.tail.handler.LineHandler;
import com.orion.tail.mode.FileNotFoundMode;
import com.orion.tail.mode.FileOffsetMode;
import com.orion.utils.Threads;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * tracker 方式
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/18 17:36
 */
public class TrackerTailFileHandler implements ITailHandler, LineHandler {

    @Getter
    private String token;

    /**
     * session
     */
    private WebSocketSession session;

    /**
     * hint
     */
    private TailFileHint hint;

    private DelayTracker tracker;

    public TrackerTailFileHandler(String token, WebSocketSession session, TailFileHint hint) {
        this.token = token;
        this.session = session;
        this.hint = hint;
        this.tracker = new DelayTracker(hint.getPath(), this);
        tracker.charset(hint.getCharset());
        tracker.offset(FileOffsetMode.LINE, hint.getOffset());
        tracker.notFoundMode(FileNotFoundMode.WAIT_COUNT, 10);
    }

    @Override
    public void start() {
        Threads.start(() -> {
            tracker.tail();
        }, SchedulerPools.TAIL_SCHEDULER);
    }

    @Override
    public void close() {
        tracker.stop();
    }

    @SneakyThrows
    @Override
    public void readLine(String read, int line, Tracker tracker) {
        if (session.isOpen()) {
            session.sendMessage(new TextMessage(read));
        }
    }

    @Override
    public String toString() {
        return hint.getPath();
    }
}
