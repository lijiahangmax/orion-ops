package com.orion.ops.handler.terminal;

import com.orion.ops.consts.protocol.TerminalOperate;
import com.orion.ops.entity.dto.TerminalDataTransferDTO;

/**
 * 操作处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/17 22:56
 */
public interface IOperateHandler {

    /**
     * 处理消息
     *
     * @param data    data
     * @param operate 操作
     * @throws Exception ex
     */
    void handle(TerminalDataTransferDTO data, TerminalOperate operate) throws Exception;

}
