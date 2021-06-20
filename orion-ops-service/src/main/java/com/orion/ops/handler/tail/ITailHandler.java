package com.orion.ops.handler.tail;

import com.orion.able.SafeCloseable;

/**
 * tail 接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/18 17:05
 */
public interface ITailHandler extends SafeCloseable {

    /**
     * 开始
     */
    void start();

}
