package com.orion.ops.handler.release;

import com.orion.able.Executable;
import com.orion.able.SafeCloseable;
import com.orion.function.select.Branches;
import com.orion.function.select.Selector;
import com.orion.ops.consts.app.ReleaseSerialType;
import com.orion.ops.entity.domain.ApplicationReleaseDO;

/**
 * 发布处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/28 17:12
 */
public interface IReleaseProcessor extends Executable, Runnable, SafeCloseable {

    /**
     * 获取id
     *
     * @return id
     */
    Long getReleaseId();

    /**
     * 终止
     */
    void terminated();

    /**
     * 获取发布执行器
     *
     * @param release release
     * @return 执行器
     */
    static IReleaseProcessor with(ApplicationReleaseDO release) {
        return Selector.<ReleaseSerialType, IReleaseProcessor>of(ReleaseSerialType.of(release.getReleaseSerialize()))
                .test(Branches.eq(ReleaseSerialType.SERIAL)
                        .then(() -> new SerialReleaseProcessor(release.getId())))
                .test(Branches.eq(ReleaseSerialType.PARALLEL)
                        .then(() -> new ParallelReleaseProcessor(release.getId())))
                .get();
    }

}
