package com.orion.ops.handler.tail.impl;

import com.alibaba.fastjson.JSON;
import com.orion.lang.thread.HookRunnable;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.ws.WsCloseCode;
import com.orion.ops.handler.tail.ITailHandler;
import com.orion.ops.handler.tail.TailFileHint;
import com.orion.tail.Tracker;
import com.orion.tail.delay.DelayTrackerListener;
import com.orion.tail.handler.DataHandler;
import com.orion.tail.mode.FileNotFoundMode;
import com.orion.tail.mode.FileOffsetMode;
import com.orion.utils.Threads;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * tracker 方式
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/18 17:36
 */
@Slf4j
public class TrackerTailFileHandler implements ITailHandler, DataHandler {

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

    private DelayTrackerListener tracker;

    private volatile boolean close;

    @Getter
    private String filePath;

    public TrackerTailFileHandler(TailFileHint hint, WebSocketSession session) {
        this.token = hint.getToken();
        this.hint = hint;
        this.filePath = hint.getPath();
        this.session = session;
        log.info("tail TRACKER 监听文件初始化 token: {}, hint: {}", token, JSON.toJSONString(hint));
    }

    @Override
    public void start() {
        this.tracker = new DelayTrackerListener(filePath, this);
        tracker.delayMillis(hint.getDelay());
        tracker.charset(hint.getCharset());
        tracker.offset(FileOffsetMode.LINE, hint.getOffset());
        tracker.notFoundMode(FileNotFoundMode.WAIT_COUNT, 10);
        Threads.start(new HookRunnable(() -> {
            log.info("tail TRACKER 开始监听文件 token: {}", token);
            tracker.tail();
        }, this::callback), SchedulerPools.TAIL_SCHEDULER);
    }

    @Override
    public void setLastModify() {
        tracker.setFileLastModifyTime();
    }

    @Override
    public Long getMachineId() {
        return Const.HOST_MACHINE_ID;
    }

    /**
     * 回调
     */
    @SneakyThrows
    private void callback() {
        log.info("tail TRACKER 监听文件结束 token: {}", token);
        if (session.isOpen()) {
            session.close(WsCloseCode.EOF_CALLBACK.status());
        }
    }

    @SneakyThrows
    @Override
    public void read(byte[] bytes, int len, Tracker tracker) {
        if (session.isOpen()) {
            session.sendMessage(new BinaryMessage(bytes, 0, len, true));
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

    @Override
    public String toString() {
        return filePath;
    }

}
