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
package cn.orionsec.ops.handler.terminal;

import cn.orionsec.kit.lang.constant.Letters;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.kit.lang.utils.io.Streams;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.kit.net.host.SessionStore;
import cn.orionsec.kit.net.host.ssh.shell.ShellExecutor;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.SchedulerPools;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.constant.terminal.TerminalClientOperate;
import cn.orionsec.ops.constant.terminal.TerminalConst;
import cn.orionsec.ops.constant.ws.WsCloseCode;
import cn.orionsec.ops.constant.ws.WsProtocol;
import cn.orionsec.ops.entity.config.TerminalConnectConfig;
import cn.orionsec.ops.entity.domain.MachineTerminalLogDO;
import cn.orionsec.ops.entity.dto.terminal.TerminalSizeDTO;
import cn.orionsec.ops.handler.terminal.screen.TerminalScreenEnv;
import cn.orionsec.ops.handler.terminal.screen.TerminalScreenHeader;
import cn.orionsec.ops.handler.terminal.watcher.ITerminalWatcherProcessor;
import cn.orionsec.ops.handler.terminal.watcher.TerminalWatcherProcessor;
import cn.orionsec.ops.service.api.MachineTerminalService;
import cn.orionsec.ops.utils.PathBuilders;
import cn.orionsec.ops.utils.Utils;
import cn.orionsec.ops.utils.WebSockets;
import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
    private final TerminalConnectConfig hint;

    @Getter
    private final ITerminalWatcherProcessor watcher;

    private final WebSocketSession session;

    private final SessionStore sessionStore;

    private ShellExecutor executor;

    private long connectedTime;

    /**
     * 录屏流
     */
    private OutputStream screenStream;

    /**
     * 最后一次心跳通讯时间
     */
    private volatile long lastHeartbeat;

    protected volatile boolean close;

    public TerminalOperateHandler(String token, TerminalConnectConfig hint, WebSocketSession session, SessionStore sessionStore) {
        this.token = token;
        this.hint = hint;
        this.watcher = new TerminalWatcherProcessor();
        this.session = session;
        this.sessionStore = sessionStore;
        this.lastHeartbeat = System.currentTimeMillis();
        this.initShell();
    }

    @Override
    public void connect() {
        executor.connect();
        executor.streamHandler(this::streamHandler);
        // 连接成功后初始化日志信息
        this.initLog();
        // 开始监听输出
        SchedulerPools.TERMINAL_SCHEDULER.execute(executor);
        watcher.watch();
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
                byte[] msg = WsProtocol.OK.msg(bs, 0, read);
                WebSockets.sendText(session, msg);
                // 响应监视
                watcher.sendMessage(msg);
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
            WebSockets.close(session, WsCloseCode.READ_EXCEPTION);
        }
        // eof
        if (close) {
            return;
        }
        WebSockets.close(session, WsCloseCode.EOF);
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
            log.error("terminal 断开连接 失败 token: {}", token, e);
        }
    }

    @Override
    public void forcedOffline() {
        WebSockets.close(session, WsCloseCode.FORCED_OFFLINE);
        log.info("terminal 管理员强制断连 {}", token);
    }

    @Override
    public void heartbeatDownClose() {
        WebSockets.close(session, WsCloseCode.HEART_DOWN);
        log.info("terminal 心跳结束断连 {}", token);
    }

    @Override
    public void sendHeartbeat() {
        WebSockets.sendText(session, WsProtocol.PING.get());
    }

    @Override
    public boolean isDown() {
        return (System.currentTimeMillis() - lastHeartbeat) > TerminalConst.TERMINAL_CONNECT_DOWN;
    }

    @Override
    public void handleMessage(TerminalClientOperate operate, String body) {
        if (close) {
            return;
        }
        switch (operate) {
            case KEY:
                executor.write(Strings.bytes(body));
                return;
            case PING:
                // client 主动发送 ping
                this.lastHeartbeat = System.currentTimeMillis();
                WebSockets.sendText(session, WsProtocol.PONG.get());
                return;
            case PONG:
                // server 主动发送 ping, client 响应 pong
                this.lastHeartbeat = System.currentTimeMillis();
                return;
            case RESIZE:
                this.resize(body);
                return;
            case COMMAND:
                executor.write(Strings.bytes(body));
                executor.write(new byte[]{Letters.LF});
            case CLEAR:
                executor.write(new byte[]{12});
                return;
            default:
        }
    }

    @Override
    public void close() {
        this.disconnect();
        Streams.close(watcher);
    }

    /**
     * 重置大小
     */
    private void resize(String body) {
        // 检查参数
        TerminalSizeDTO window = TerminalUtils.parseResizeBody(body);
        if (window == null) {
            WebSockets.sendText(session, WsProtocol.ERROR.get());
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

}
