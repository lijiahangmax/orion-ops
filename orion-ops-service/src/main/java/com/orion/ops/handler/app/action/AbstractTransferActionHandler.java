package com.orion.ops.handler.app.action;

import com.orion.remote.channel.BaseExecutor;
import com.orion.utils.io.Streams;

/**
 * 执行操作-传输产物
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.consts.app.ActionType#RELEASE_TRANSFER
 * @since 2022/2/11 16:01
 */
public abstract class AbstractTransferActionHandler<E extends BaseExecutor> extends AbstractActionHandler {

    protected E executor;

    public AbstractTransferActionHandler(Long actionId, MachineActionStore store) {
        super(actionId, store);
    }

    @Override
    public void terminate() {
        super.terminate();
        // 关闭executor
        Streams.close(executor);
    }

    @Override
    public void close() {
        super.close();
        // 关闭executor
        Streams.close(executor);
    }

}
