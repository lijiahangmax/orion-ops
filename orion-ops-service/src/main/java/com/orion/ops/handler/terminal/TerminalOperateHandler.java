package com.orion.ops.handler.terminal;

import com.orion.ops.consts.Const;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.consts.terminal.TerminalConst;
import com.orion.ops.consts.terminal.TerminalOperate;
import com.orion.ops.consts.ws.WsCloseCode;
import com.orion.ops.consts.ws.WsProtocol;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.ops.entity.dto.TerminalSizeDTO;
import com.orion.ops.service.api.MachineTerminalService;
import com.orion.ops.utils.PathBuilders;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.ssh.BaseRemoteExecutor;
import com.orion.remote.channel.ssh.ShellExecutor;
import com.orion.spring.SpringHolder;
import com.orion.utils.Arrays1;
import com.orion.utils.Strings;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 终端处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/17 22:46
 */
@Slf4j
public class TerminalOperateHandler implements IOperateHandler {

    private static final MachineTerminalService machineTerminalService = SpringHolder.getBean(MachineTerminalService.class);

    @Getter
    private String token;

    /**
     * 终端配置
     */
    @Getter
    private TerminalConnectHint hint;

    /**
     * ws
     */
    private WebSocketSession session;

    /**
     * sessionStore
     */
    private SessionStore sessionStore;

    /**
     * 执行器
     */
    private ShellExecutor executor;

    /**
     * 日志流
     */
    private OutputStream logStream;

    /**
     * 最后一次发送心跳的时间
     */
    private volatile long lastPing;

    protected volatile boolean close;

    public TerminalOperateHandler(String token, TerminalConnectHint hint, WebSocketSession session, SessionStore sessionStore) {
        this.token = token;
        this.hint = hint;
        this.session = session;
        this.sessionStore = sessionStore;
        this.lastPing = System.currentTimeMillis();
        this.init();
    }

    /**
     * 打开session
     */
    private void init() {
        this.executor = sessionStore.getShellExecutor();
        executor.terminalType(hint.getTerminalType());
        executor.size(hint.getCols(), hint.getRows(), hint.getWidth(), hint.getHeight());
        String logPath = PathBuilders.getTerminalLogPath(hint.getUserId());
        String realLogPath = Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), logPath);
        this.logStream = Files1.openOutputStreamSafe(realLogPath);
        log.info("terminal 开始记录用户操作日志: {} {}", token, logPath);
        // 记录日志
        MachineTerminalLogDO logEntity = new MachineTerminalLogDO();
        logEntity.setAccessToken(token);
        logEntity.setUserId(hint.getUserId());
        logEntity.setUsername(hint.getUsername());
        logEntity.setMachineId(hint.getMachineId());
        logEntity.setMachineName(hint.getMachineName());
        logEntity.setMachineHost(hint.getMachineHost());
        logEntity.setConnectedTime(hint.getConnectedTime());
        logEntity.setOperateLogFile(logPath);
        Long logId = machineTerminalService.addAccessLog(logEntity);
        hint.setLogId(logId);
        log.info("terminal 用户操作日志入库: {} logId: {}", token, logId);
    }

    @Override
    public void connect() {
        executor.connect()
                .scheduler(SchedulerPools.TERMINAL_SCHEDULER)
                .callback(this::callback)
                .streamHandler(this::streamHandler)
                .exec();
    }

    /**
     * 回调
     *
     * @param executor executor
     */
    private void callback(BaseRemoteExecutor executor) {
        if (close) {
            return;
        }
        this.sendClose(WsCloseCode.EOF_CALLBACK);
        log.info("terminal eof回调 {}", token);
    }

    /**
     * 标准输入处理
     *
     * @param inputStream stream
     */
    private void streamHandler(InputStream inputStream) {
        byte[] bs = new byte[Const.BUFFER_KB_4];
        BufferedInputStream in = new BufferedInputStream(inputStream, Const.BUFFER_KB_4);
        int read;
        try {
            while (session.isOpen() && (read = in.read(bs)) != -1) {
                session.sendMessage(new TextMessage(WsProtocol.OK.msg(Arrays1.resize(bs, read))));
            }
        } catch (IOException ex) {
            log.error("terminal 读取流失败", ex);
            ex.printStackTrace();
            this.sendClose(WsCloseCode.READ_EXCEPTION);
        }
    }

    @Override
    public void disconnect() {
        if (close) {
            return;
        }
        this.close = true;
        try {
            Streams.close(logStream);
            Streams.close(executor);
            Streams.close(sessionStore);
        } catch (Exception e) {
            log.error("terminal 断开连接 失败 token: {}, {}", token, e);
        }
    }

    @Override
    public void forcedOffline() {
        this.sendClose(WsCloseCode.FORCED_OFFLINE);
        log.info("terminal 管理员强制断连 {}", token);
    }

    @Override
    public void heartDown() {
        this.sendClose(WsCloseCode.HEART_DOWN);
        log.info("terminal 心跳结束断连 {}", token);
    }

    @Override
    public boolean isDown() {
        return (System.currentTimeMillis() - lastPing) > TerminalConst.TERMINAL_CONNECT_DOWN;
    }

    @Override
    public void handleMessage(TerminalOperate operate, String body) throws Exception {
        if (close) {
            return;
        }
        switch (operate) {
            case PING:
                this.lastPing = System.currentTimeMillis();
                session.sendMessage(new TextMessage(WsProtocol.PONG.get()));
                return;
            case DISCONNECT:
                this.sendClose(WsCloseCode.DISCONNECT);
                log.info("terminal 用户主动断连 {}", token);
                return;
            case RESIZE:
                this.resize(body);
                return;
            default:
                this.handleWrite(operate, body);
        }
    }

    /**
     * 处理输入操作操作
     *
     * @param operate 操作
     * @param body    body
     */
    private void handleWrite(TerminalOperate operate, String body) throws IOException {
        if (body == null) {
            return;
        }
        byte[] bs;
        switch (operate) {
            case KEY:
                bs = Strings.bytes(body);
                break;
            case COMMAND:
                bs = Strings.bytes(body + Const.LF);
                break;
            case INTERRUPT:
                bs = new byte[]{3, 10};
                break;
            case HANGUP:
                bs = new byte[]{24, 10};
                break;
            default:
                return;
        }
        logStream.write(bs);
        executor.write(bs);
    }

    /**
     * 重置大小
     */
    private void resize(String body) throws IOException {
        // 检查参数
        TerminalSizeDTO window = TerminalSizeDTO.parse(body);
        if (window == null) {
            session.sendMessage(new TextMessage(WsProtocol.MISS_ARGUMENT.get()));
            return;
        }
        hint.setCols(window.getCols());
        hint.setRows(window.getRows());
        hint.setWidth(window.getWidth());
        hint.setHeight(window.getHeight());
        if (!executor.isConnected()) {
            executor.connect();
        }
        executor.size(window.getCols(), window.getRows(), window.getWidth(), window.getHeight());
        executor.resize();
    }

    /**
     * 发送关闭连接命令
     *
     * @param code close
     */
    private void sendClose(WsCloseCode code) {
        if (session.isOpen()) {
            try {
                session.close(code.close());
            } catch (IOException e) {
                log.error("terminal 发送断开连接命令 失败 token: {}, code: {}, e: {}", token, code.getCode(), e);
            }
        }
    }

}
