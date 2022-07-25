package com.orion.ops.handler.app.release;

import com.orion.lang.able.Executable;
import com.orion.lang.able.SafeCloseable;
import com.orion.lang.function.select.Branches;
import com.orion.lang.function.select.Selector;
import com.orion.ops.constant.SerialType;
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
    void terminateAll();

    /**
     * 终止机器操作
     *
     * @param releaseMachineId releaseMachineId
     */
    void terminateMachine(Long releaseMachineId);

    /**
     * 跳过机器操作
     *
     * @param releaseMachineId 机器id
     */
    void skipMachine(Long releaseMachineId);

    /**
     * 输入机器命令
     *
     * @param releaseMachineId 机器id
     * @param command          command
     */
    void writeMachine(Long releaseMachineId, String command);

    /**
     * 获取发布执行器
     *
     * @param release release
     * @return 执行器
     */
    static IReleaseProcessor with(ApplicationReleaseDO release) {
        return Selector.<SerialType, IReleaseProcessor>of(SerialType.of(release.getReleaseSerialize()))
                .test(Branches.eq(SerialType.SERIAL)
                        .then(() -> new SerialReleaseProcessor(release.getId())))
                .test(Branches.eq(SerialType.PARALLEL)
                        .then(() -> new ParallelReleaseProcessor(release.getId())))
                .get();
    }

}
