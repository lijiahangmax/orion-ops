package com.orion.ops.handler.exec;

import com.orion.able.SafeCloseable;
import com.orion.function.select.Branches;
import com.orion.function.select.Selector;
import com.orion.ops.consts.command.ExecType;

/**
 * 命令执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 22:54
 */
public interface IExecHandler extends Runnable, SafeCloseable {

    /**
     * 提交任务
     *
     * @param hint hint
     * @return execId
     */
    Long submit(ExecHint hint);

    /**
     * 获取实际执行 handler
     *
     * @param hint hint
     * @return handler
     */
    static IExecHandler with(ExecHint hint) {
        return Selector.<ExecType, IExecHandler>of(hint.getExecType())
                .test(Branches.eq(ExecType.BATCH_EXEC).then(new CommandExecHandler(hint)))
                .or(new CommandExecHandler(hint));
    }

}
