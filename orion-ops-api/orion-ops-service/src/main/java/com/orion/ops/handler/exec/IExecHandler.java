package com.orion.ops.handler.exec;

import com.orion.lang.able.Executable;
import com.orion.lang.able.SafeCloseable;

/**
 * 命令执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 22:54
 */
public interface IExecHandler extends Runnable, Executable, SafeCloseable {

    /**
     * 写入
     *
     * @param out out
     */
    void write(String out);

    /**
     * 停止
     */
    void terminate();

    /**
     * 获取实际执行 handler
     *
     * @param execId execId
     * @return handler
     */
    static IExecHandler with(Long execId) {
        return new CommandExecHandler(execId);
    }

}
