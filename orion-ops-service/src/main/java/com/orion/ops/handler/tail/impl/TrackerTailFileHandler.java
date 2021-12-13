package com.orion.ops.handler.tail.impl;

import com.alibaba.fastjson.JSON;
import com.orion.lang.thread.HookRunnable;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.ws.WsCloseCode;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * tracker 方式
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/18 17:36
 */
@Slf4j
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

    private volatile boolean close;

    public TrackerTailFileHandler(TailFileHint hint, WebSocketSession session) {
        this.token = hint.getToken();
        this.hint = hint;
        this.session = session;
        log.info("tail TRACKER 监听文件初始化 token: {}, hint: {}", token, JSON.toJSONString(hint));
    }

    @Override
    public void start() {
        this.tracker = new DelayTracker(hint.getPath(), this);
        tracker.delayMillis(Const.TRACKER_DELAY_MS);
        tracker.charset(hint.getCharset());
        tracker.offset(FileOffsetMode.LINE, hint.getOffset());
        tracker.notFoundMode(FileNotFoundMode.WAIT_COUNT, 10);
        Threads.start(new HookRunnable(() -> {
            log.info("tail TRACKER 开始监听文件 token: {}", token);
            tracker.tail();
        }, this::callback), SchedulerPools.TAIL_SCHEDULER);
    }

    @Override
    public Long getMachineId() {
        return Const.HOST_MACHINE_ID;
    }

    @Override
    public String getFilePath() {
        return hint.getPath();
    }

    /**
     * 回调
     */
    @SneakyThrows
    private void callback() {
        log.info("tail TRACKER 监听文件结束 token: {}", token);
        if (session.isOpen()) {
            session.close(WsCloseCode.EOF_CALLBACK.close());
        }
    }

    @Override
    @SneakyThrows
    public void close() {
        if (close) {
            return;
        }
        this.close = true;
        if (tracker != null) {
            tracker.stop();
        }
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
