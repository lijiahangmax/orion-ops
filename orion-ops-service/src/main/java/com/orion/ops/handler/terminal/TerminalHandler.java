package com.orion.ops.handler.terminal;

import com.alibaba.fastjson.JSON;
import com.orion.constant.Const;
import com.orion.ops.consts.EnvAttr;
import com.orion.ops.consts.protocol.TerminalCloseCode;
import com.orion.ops.consts.protocol.TerminalConst;
import com.orion.ops.consts.protocol.TerminalOperate;
import com.orion.ops.consts.protocol.TerminalProtocol;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.ops.entity.dto.TerminalConnectDTO;
import com.orion.ops.entity.dto.TerminalDataTransferDTO;
import com.orion.ops.service.api.MachineTerminalService;
import com.orion.remote.channel.SessionStore;
import com.orion.remote.channel.ssh.ShellExecutor;
import com.orion.spring.SpringHolder;
import com.orion.utils.Arrays1;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 终端 基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/17 22:46
 */
@Slf4j
public abstract class TerminalHandler implements OperateHandler, ManagementHandler {

    private static final MachineTerminalService machineTerminalService = SpringHolder.getBean("machineTerminalService");

    @Getter
    protected String token;

    /**
     * 终端配置
     */
    @Getter
    protected TerminalConnectConfig config;

    /**
     * ws
     */
    protected WebSocketSession session;

    /**
     * sessionStore
     */
    protected SessionStore sessionStore;

    /**
     * 执行器
     */
    protected ShellExecutor executor;

    /**
     * 日志流
     */
    protected OutputStream logStream;

    /**
     * 最后一次发送心跳的时间
     */
    private volatile long lastPing;

    protected volatile boolean close;

    protected volatile boolean callback;

    public TerminalHandler(String token, TerminalConnectConfig config, WebSocketSession session, SessionStore sessionStore) {
        this.token = token;
        this.config = config;
        this.session = session;
        this.sessionStore = sessionStore;
        this.lastPing = System.currentTimeMillis();
        this.callback = true;
        this.open();
    }

    /**
     * 打开session
     */
    private void open() {
        this.executor = sessionStore.getShellExecutor();
        executor.terminalType(config.getTerminalType());
        executor.size(config.getCols(), config.getRows(), config.getWidth(), config.getHeight());
        String logPath = EnvAttr.LOG_PATH.getValue() + "/" + TerminalConst.TERMINAL + "/"
                + Dates.current(Dates.YMDHMS2) + "_" + MachineTerminalService.getTokenUserId(token) + ".log";
        this.logStream = Files1.openOutputStreamSafe(Files1.getPath(logPath));
        log.info("terminal 开始记录用户操作日志: {} {}", token, logPath);
        // 记录日志
        MachineTerminalLogDO logEntity = new MachineTerminalLogDO();
        logEntity.setAccessToken(token);
        logEntity.setUserId(config.getUserId());
        logEntity.setMachineId(config.getMachineId());
        logEntity.setMachineHost(config.getMachineHost());
        logEntity.setConnectedTime(config.getConnectedTime());
        logEntity.setOperateLogFile(logPath);
        Long logId = machineTerminalService.addAccessLog(logEntity);
        config.setLogId(logId);
        log.info("terminal 用户操作日志入库: {} logId: {}", token, logId);
    }

    /**
     * 建立连接
     */
    public void connect() {
        executor.connect(TerminalConst.TERMINAL_CONNECT_TIMEOUT);
        executor.scheduler(TerminalConst.TERMINAL_SCHEDULER)
                .callback(d -> {
                    if (!this.callback) {
                        return;
                    }
                    this.disconnect();
                    this.sendClose(TerminalCloseCode.EOF_CALLBACK);
                    log.info("terminal eof回调 {}", token);
                });
        executor.streamHandler((k, i) -> {
            byte[] bs = new byte[Const.BUFFER_KB_2];
            BufferedInputStream in = new BufferedInputStream(i, Const.BUFFER_KB_2);
            int read;
            try {
                while (session.isOpen() && (read = in.read(bs)) != -1) {
                    session.sendMessage(new TextMessage(TerminalProtocol.OK.msg(Arrays1.resize(bs, read))));
                }
            } catch (IOException ex) {
                if (session.isOpen()) {
                    try {
                        session.close(TerminalCloseCode.READ_EXCEPTION.close());
                    } catch (Exception ex1) {
                        log.error("terminal 处理流失败 关闭连接失败", ex1);
                    }
                } else {
                    log.error("terminal 读取流失败", ex);
                }
            }
        }).exec();
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (close) {
            return;
        }
        this.close = true;
        try {
            Streams.close(logStream);
            executor.disconnectChannel();
            sessionStore.disconnect();
        } catch (Exception e) {
            log.error("terminal 断开连接 失败 token: {}, {}", token, e);
        }
    }

    /**
     * 发送关闭连接命令
     *
     * @param code close
     */
    private void sendClose(TerminalCloseCode code) {
        if (session.isOpen()) {
            try {
                session.close(code.close());
            } catch (IOException e) {
                log.error("terminal 发送断开连接命令 失败 token: {}, {}", token, e);
            }
        }
    }

    /**
     * 重置大小
     */
    protected void resize(String body) {
        // 检查参数
        TerminalConnectDTO window = null;
        try {
            window = JSON.parseObject(body, TerminalConnectDTO.class);
        } catch (Exception e) {
            // ignore
        }
        if (window == null) {
            try {
                session.sendMessage(new TextMessage(TerminalProtocol.ARGUMENT.get()));
            } catch (Exception e) {
                // ignore
            }
            return;
        }
        config.setCols(window.getCols());
        config.setRows(window.getRows());
        config.setWidth(window.getWidth());
        config.setHeight(window.getHeight());
        executor.size(window.getCols(), window.getRows(), window.getWidth(), window.getHeight());
    }

    /**
     * 处理消息 (对外)
     *
     * @param data    data
     * @param operate 操作
     * @throws Exception ex
     */
    public void handleMessage(TerminalDataTransferDTO data, TerminalOperate operate) throws Exception {
        if (close) {
            return;
        }
        switch (operate) {
            case PING:
                this.lastPing = System.currentTimeMillis();
                session.sendMessage(new TextMessage(TerminalProtocol.PONG.get()));
                return;
            case RESIZE:
                this.resize(data.getBody());
                return;
            case DISCONNECT:
                this.disconnect();
                this.sendClose(TerminalCloseCode.DISCONNECT);
                log.info("terminal 用户主动断连 {}", token);
                return;
            default:
                this.handle(data, operate);
        }
    }

    /**
     * 认证
     *
     * @param id id
     * @return 是否认证成功
     */
    public boolean valid(String id) {
        return config.getSessionId().equals(id);
    }

    /**
     * 心跳是否结束
     *
     * @return true结束
     */
    public boolean isDown() {
        return (System.currentTimeMillis() - lastPing) > TerminalConst.TERMINAL_CONNECT_DOWN;
    }

}
