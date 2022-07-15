package com.orion.ops.handler.app.pipeline;

import com.orion.lang.able.Executable;

/**
 * 流水线处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/15 14:18
 */
public interface IPipelineProcessor extends Executable, Runnable {

    /**
     * 获取明细id
     *
     * @return taskId
     */
    Long getTaskId();

    /**
     * 停止执行
     */
    void terminate();

    /**
     * 停止执行详情
     *
     * @param id id
     */
    void terminateDetail(Long id);

    /**
     * 跳过执行详情
     *
     * @param id id
     */
    void skipDetail(Long id);

    /**
     * 获取流水线执行器
     *
     * @param id id
     * @return 流水线执行器
     */
    static IPipelineProcessor with(Long id) {
        return new PipelineProcessor(id);
    }

}
