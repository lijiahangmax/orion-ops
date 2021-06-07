package com.orion.ops.handler.terminal;

import com.orion.ops.consts.Const;
import com.orion.ops.consts.protocol.TerminalCloseCode;
import com.orion.ops.consts.protocol.TerminalOperate;
import com.orion.ops.entity.dto.TerminalDataTransferDTO;
import com.orion.remote.channel.SessionStore;
import com.orion.utils.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

/**
 * 访问终端处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 22:54
 */
@Slf4j
public class TerminalOperateHandler extends AbstractTerminalHandler {

    public TerminalOperateHandler(String token, TerminalConnectConfig config, WebSocketSession session, SessionStore sessionStore) {
        super(token, config, session, sessionStore);
    }

    @Override
    public void handle(TerminalDataTransferDTO data, TerminalOperate operate) {
        String body = data.getBody();
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
        try {
            logStream.write(bs);
            executor.write(bs);
        } catch (Exception e) {
            log.info("terminal 处理operate失败 token: {} {}", token, e);
        }
    }

    @Override
    public void forcedOffline() throws Exception {
        this.callback = false;
        this.disconnect();
        session.close(TerminalCloseCode.FORCED_OFFLINE.close());
        log.info("terminal 管理员强制断连 {}", token);
    }

    @Override
    public void heartDown() throws Exception {
        this.callback = false;
        this.disconnect();
        session.close(TerminalCloseCode.HEART_DOWN.close());
        log.info("terminal 心跳结束断连 {}", token);
    }

}
