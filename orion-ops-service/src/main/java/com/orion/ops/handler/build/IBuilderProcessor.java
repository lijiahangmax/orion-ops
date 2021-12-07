package com.orion.ops.handler.build;

import com.orion.able.Executable;
import com.orion.able.SafeCloseable;

/**
 * 构建执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/5 22:05
 */
public interface IBuilderProcessor extends Executable, Runnable, SafeCloseable {

    /**
     * 终止
     */
    void terminated();

    /**
     * 创建执行器
     *
     * @param buildId buildId
     * @return processor
     */
    static IBuilderProcessor with(Long buildId) {
        return new BuilderProcessor(buildId);
    }

}
