package com.orion.ops.handler.tail.impl;

import com.alibaba.fastjson.JSON;
import com.orion.constant.Letters;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.ws.WsCloseCode;
import com.orion.ops.handler.tail.ITailHandler;
import com.orion.ops.handler.tail.TailFileHint;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.utils.WebSockets;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.ssh.BaseRemoteExecutor;
import com.orion.remote.channel.ssh.CommandExecutor;
import com.orion.spring.SpringHolder;
import com.orion.utils.io.Streams;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * tail -f 命令
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/20 18:44
 */
@Slf4j
public class ExecTailFileHandler implements ITailHandler {

    protected static MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

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

    private SessionStore sessionStore;

    private CommandExecutor executor;

    private volatile boolean close;

    public ExecTailFileHandler(TailFileHint hint, WebSocketSession session) {
        this.token = hint.getToken();
        this.hint = hint;
        this.session = session;
        log.info("tail EXEC_TAIL 监听文件初始化 token: {}, hint: {}", token, JSON.toJSONString(hint));
    }

    @Override
    public void start() throws Exception {
        try {
            // 打开session
            this.sessionStore = machineInfoService.openSessionStore(hint.getMachineId());
            log.info("tail 建立连接成功 machineId: {}", hint.getMachineId());
        } catch (Exception e) {
            WebSockets.openSessionStoreThrowClose(session, e);
            log.error("tail 建立连接失败-连接远程服务器失败 e: {}, machineId: {}", e, hint.getMachineId());
            return;
        }
        // 打开 command
        this.executor = sessionStore.getCommandExecutor(hint.getCommand());
        executor.inherit()
                .scheduler(SchedulerPools.TAIL_SCHEDULER)
                .callback(this::callback)
                .streamHandler(this::handler)
                .connect()
                .exec();
        log.info("tail EXEC_TAIL 监听文件开始 token: {}", token);
    }

    @Override
    public Long getMachineId() {
        return hint.getMachineId();
    }

    @Override
    public String getFilePath() {
        return hint.getPath();
    }

    /**
     * 回调
     *
     * @param executor executor
     */
    @SneakyThrows
    private void callback(BaseRemoteExecutor executor) {
        log.info("tail EXEC_TAIL 监听文件结束 token: {}", token);
        if (session.isOpen()) {
            session.close(WsCloseCode.EOF_CALLBACK.close());
        }
    }

    /**
     * 处理标准输入流
     *
     * @param inputStream 流
     */
    @SneakyThrows
    private void handler(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, hint.getCharset()), Const.BUFFER_KB_8);
        char[] buffer = new char[Const.BUFFER_KB_4];
        int read;
        StringBuilder sb = new StringBuilder();
        // tail命令结合BufferedReader对CR处理有问题 所以不能使用readLine
        while ((read = reader.read(buffer)) != -1) {
            int mark = -1;
            for (int i = 0; i < read; i++) {
                // 读取到行结尾
                if (buffer[i] == Letters.LF) {
                    sb.append(buffer, mark + 1, i - mark - 1);
                    if (session.isOpen()) {
                        String payload = sb.toString().replaceAll(Const.CR, Const.EMPTY);
                        session.sendMessage(new TextMessage(payload));
                    }
                    sb = new StringBuilder();
                    mark = i;
                }
            }
            // 不是完整的一行
            if (mark == -1) {
                sb.append(buffer, 0, read);
            } else if (mark != read - 1) {
                sb.append(buffer, mark + 1, read - mark - 1);
            }
        }
    }

    @Override
    @SneakyThrows
    public void close() {
        if (close) {
            return;
        }
        this.close = true;
        Streams.close(executor);
        Streams.close(sessionStore);
    }

    @Override
    public String toString() {
        return hint.getPath();
    }

}
