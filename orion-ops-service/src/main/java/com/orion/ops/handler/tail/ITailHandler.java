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
     *
     * @throws Exception Exception
     */
    void start() throws Exception;

    /**
     * 获取机器id
     *
     * @return 机器id
     */
    Long getMachineId();

    /**
     * 获取文件路径
     *
     * @return 文件路径
     */
    String getFilePath();

}
