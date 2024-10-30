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
package cn.orionsec.ops.handler.tail.impl;

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.SchedulerPools;
import cn.orionsec.ops.constant.ws.WsCloseCode;
import cn.orionsec.ops.handler.tail.ITailHandler;
import cn.orionsec.ops.handler.tail.TailFileHint;
import cn.orionsec.ops.service.api.MachineInfoService;
import cn.orionsec.ops.utils.WebSockets;
import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.io.Streams;
import com.orion.net.remote.channel.SessionStore;
import com.orion.net.remote.channel.ssh.CommandExecutor;
import com.orion.spring.SpringHolder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.InputStream;

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
    private final String token;

    /**
     * session
     */
    private final WebSocketSession session;

    /**
     * hint
     */
    private final TailFileHint hint;

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
        this.executor = sessionStore.getCommandExecutor(Strings.replaceCRLF(hint.getCommand()));
        executor.inherit();
        executor.scheduler(SchedulerPools.TAIL_SCHEDULER);
        executor.callback(this::callback);
        executor.streamHandler(this::handler);
        executor.connect();
        executor.exec();
        log.info("tail EXEC_TAIL 监听文件开始 token: {}", token);
    }

    @Override
    public void write(String command) {
        executor.write(command);
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
     */
    private void callback() {
        log.info("tail EXEC_TAIL 监听文件结束 token: {}", token);
        WebSockets.close(session, WsCloseCode.EOF);
    }

    /**
     * 处理标准输入流
     *
     * @param input 流
     */
    @SneakyThrows
    private void handler(InputStream input) {
        byte[] buffer = new byte[Const.BUFFER_KB_8];
        int read;
        while ((read = input.read(buffer)) != -1) {
            if (session.isOpen()) {
                session.sendMessage(new BinaryMessage(buffer, 0, read, true));
            }
        }

        // 2.0 tail 换行有问题
        // BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, hint.getCharset()), Const.BUFFER_KB_8);
        // String line;
        // while ((line = reader.readLine()) != null) {
        //     session.sendMessage(new TextMessage(line));
        // }

        // 1.0 tracker 换行有问题
        // char[] buffer = new char[Const.BUFFER_KB_4];
        // int read;
        // StringBuilder sb = new StringBuilder();
        // // tail命令结合BufferedReader对CR处理有问题 所以不能使用readLine
        // while ((read = reader.read(buffer)) != -1) {
        //     int mark = -1;
        //     for (int i = 0; i < read; i++) {
        //         // 读取到行结尾
        //         if (buffer[i] == Letters.LF) {
        //             sb.append(buffer, mark + 1, i - mark - 1);
        //             if (session.isOpen()) {
        //                 String payload = sb.toString().replaceAll(Const.CR, Const.EMPTY);
        //                 session.sendMessage(new TextMessage(payload));
        //             }
        //             sb = new StringBuilder();
        //             mark = i;
        //         }
        //     }
        //     // 不是完整的一行
        //     if (mark == -1) {
        //         sb.append(buffer, 0, read);
        //     } else if (mark != read - 1) {
        //         sb.append(buffer, mark + 1, read - mark - 1);
        //     }
        // }
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
