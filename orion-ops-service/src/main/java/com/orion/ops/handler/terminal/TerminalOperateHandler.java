package com.orion.ops.handler.terminal;

import com.alibaba.fastjson.JSON;
import com.orion.lang.constant.Letters;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.io.Streams;
import com.orion.lang.utils.time.Dates;
import com.orion.net.remote.channel.SessionStore;
import com.orion.net.remote.channel.ssh.ShellExecutor;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.SchedulerPools;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.constant.terminal.TerminalConst;
import com.orion.ops.constant.terminal.TerminalOperate;
import com.orion.ops.constant.ws.WsCloseCode;
import com.orion.ops.constant.ws.WsProtocol;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.ops.entity.dto.TerminalSizeDTO;
import com.orion.ops.handler.terminal.screen.TerminalScreenEnv;
import com.orion.ops.handler.terminal.screen.TerminalScreenHeader;
import com.orion.ops.service.api.MachineTerminalService;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Utils;
import com.orion.ops.utils.WebSockets;
import com.orion.spring.SpringHolder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

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

    private static final String SCREEN_BODY_TEMPLATE = "[{}, \"o\", \"{}\"]";

    @Getter
    private final String token;

    @Getter
    private final TerminalConnectHint hint;

    private final WebSocketSession session;

    private final SessionStore sessionStore;

    private ShellExecutor executor;

    private long connectedTime;

    /**
     * 录屏流
     */
    private OutputStream screenStream;

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
        this.initShell();
    }

    @Override
    public void connect() {
        executor.connect();
        executor.scheduler(SchedulerPools.TERMINAL_SCHEDULER);
        executor.streamHandler(this::streamHandler);
        // 连接成功后初始化日志信息
        this.initLog();
        // 开始监听输出
        executor.exec();
    }

    /**
     * 初始化 shell
     */
    private void initShell() {
        // 初始化 shell 执行器
        this.executor = sessionStore.getShellExecutor();
        executor.terminalType(hint.getTerminalType());
        executor.size(hint.getCols(), hint.getRows());
    }

    /**
     * 初始化日志
     */
    private void initLog() {
        this.connectedTime = System.currentTimeMillis();
        hint.setConnectedTime(new Date(connectedTime));
        // 初始化录屏
        String screenPath = this.initScreenStream();
        log.info("terminal 开始记录用户操作录屏: {} {}", token, screenPath);
        // 记录日志
        MachineTerminalLogDO logEntity = new MachineTerminalLogDO();
        logEntity.setAccessToken(token);
        logEntity.setUserId(hint.getUserId());
        logEntity.setUsername(hint.getUsername());
        logEntity.setMachineId(hint.getMachineId());
        logEntity.setMachineName(hint.getMachineName());
        logEntity.setMachineTag(hint.getMachineTag());
        logEntity.setMachineHost(hint.getMachineHost());
        logEntity.setConnectedTime(hint.getConnectedTime());
        logEntity.setScreenPath(screenPath);
        Long logId = machineTerminalService.addTerminalLog(logEntity);
        hint.setLogId(logId);
        log.info("terminal 保存用户操作日志: {} logId: {}", token, logId);
    }

    /**
     * 初始化录屏流
     *
     * @return path
     */
    @SneakyThrows
    private String initScreenStream() {
        // 初始化流
        String screenPath = PathBuilders.getTerminalScreenPath(hint.getUserId(), hint.getMachineId());
        String realScreenPath = Files1.getPath(SystemEnvAttr.SCREEN_PATH.getValue(), screenPath);
        this.screenStream = Files1.openOutputStreamFastSafe(realScreenPath);
        // 设置头
        TerminalScreenHeader header = new TerminalScreenHeader();
        String title = Strings.format("{}({}) {} {}", hint.getMachineName(), hint.getMachineHost(),
                hint.getUsername(), Dates.format(hint.getConnectedTime()));
        header.setTitle(title);
        header.setCols(hint.getCols());
        header.setRows(hint.getRows());
        header.setTimestamp(connectedTime / Dates.SECOND_STAMP);
        header.setEnv(new TerminalScreenEnv(hint.getTerminalType()));
        // 拼接头
        screenStream.write(JSON.toJSONBytes(header));
        screenStream.write(Letters.LF);
        screenStream.flush();
        return screenPath;
    }

    /**
     * 标准输出处理
     *
     * @param inputStream stream
     */
    private void streamHandler(InputStream inputStream) {
        byte[] bs = new byte[Const.BUFFER_KB_4];
        BufferedInputStream in = new BufferedInputStream(inputStream, Const.BUFFER_KB_4);
        int read;
        try {
            while (session.isOpen() && (read = in.read(bs)) != -1) {
                // 响应
                WebSockets.sendText(session, WsProtocol.OK.msg(bs, 0, read));
                // 记录录屏
                String row = Strings.format(SCREEN_BODY_TEMPLATE,
                        ((double) (System.currentTimeMillis() - connectedTime)) / Dates.SECOND_STAMP,
                        Utils.convertControlUnicode(new String(bs, 0, read)));
                screenStream.write(Strings.bytes(row));
                screenStream.write(Letters.LF);
                screenStream.flush();
            }
        } catch (IOException ex) {
            log.error("terminal 读取流失败", ex);
            this.sendClose(WsCloseCode.READ_EXCEPTION);
        }
        // eof
        if (close) {
            return;
        }
        this.sendClose(WsCloseCode.EOF_CALLBACK);
        log.info("terminal eof回调 {}", token);
    }

    @Override
    public void disconnect() {
        if (close) {
            return;
        }
        this.close = true;
        try {
            Streams.close(screenStream);
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
            case KEY:
                executor.write(Strings.bytes(body));
                return;
            case PING:
                this.lastPing = System.currentTimeMillis();
                session.sendMessage(new TextMessage(WsProtocol.PONG.get()));
                return;
            case RESIZE:
                this.resize(body);
                return;
            case COMMAND:
                executor.write(Strings.bytes(body));
                executor.write(new byte[]{Letters.LF});
                return;
            case DISCONNECT:
                this.sendClose(WsCloseCode.DISCONNECT);
                log.info("terminal 用户主动断连 {}", token);
                return;
            default:
        }
    }

    /**
     * 重置大小
     */
    private void resize(String body) throws IOException {
        // 检查参数
        TerminalSizeDTO window = TerminalSizeDTO.parse(body);
        if (window == null) {
            session.sendMessage(new TextMessage(WsProtocol.ERROR.get()));
            return;
        }
        hint.setCols(window.getCols());
        hint.setRows(window.getRows());
        if (!executor.isConnected()) {
            executor.connect();
        }
        executor.size(window.getCols(), window.getRows());
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
                session.close(code.status());
            } catch (IOException e) {
                log.error("terminal 发送断开连接命令 失败 token: {}, code: {}, e: {}", token, code.getCode(), e);
            }
        }
    }

}
