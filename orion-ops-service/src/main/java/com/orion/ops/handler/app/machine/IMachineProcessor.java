package com.orion.ops.handler.app.machine;

import com.orion.able.SafeCloseable;

/**
 * 机器执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/12 13:38
 */
public interface IMachineProcessor extends Runnable, SafeCloseable {

    /**
     * 终止
     */
    void terminate();

    /**
     * 跳过
     */
    default void skip() {
    }

}
