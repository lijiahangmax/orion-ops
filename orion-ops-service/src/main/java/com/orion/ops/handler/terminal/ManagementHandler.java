package com.orion.ops.handler.terminal;

/**
 * 管理handler
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 23:21
 */
public interface ManagementHandler {

    /**
     * 管理员强制下线
     *
     * @throws Exception Exception
     */
    void forcedOffline() throws Exception;

    /**
     * 心跳结束下线
     *
     * @throws Exception Exception
     */
    void heartDown() throws Exception;

}
