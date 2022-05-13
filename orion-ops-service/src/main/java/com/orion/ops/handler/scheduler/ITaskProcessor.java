package com.orion.ops.handler.scheduler;

import com.orion.able.SafeCloseable;
import com.orion.function.select.Branches;
import com.orion.function.select.Selector;
import com.orion.ops.consts.SerialType;

/**
 * 任务处理器基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/24 20:59
 */
public interface ITaskProcessor extends Runnable, SafeCloseable {

    /**
     * 停止全部
     */
    void terminateAll();

    /**
     * 停止机器操作
     *
     * @param recordMachineId recordMachineId
     */
    void terminateMachine(Long recordMachineId);

    /**
     * 跳过机器操作
     *
     * @param recordMachineId recordMachineId
     */
    void skipMachine(Long recordMachineId);

    /**
     * 发送机器命令
     *
     * @param recordMachineId recordMachineId
     * @param command         command
     */
    void writeMachine(Long recordMachineId, String command);

    /**
     * 获取实际执行处理器
     *
     * @param recordId recordId
     * @param type     type
     * @return 处理器
     */
    static ITaskProcessor with(Long recordId, SerialType type) {
        return Selector.<SerialType, ITaskProcessor>of(type)
                .test(Branches.eq(SerialType.SERIAL)
                        .then(() -> new SerialTaskProcessor(recordId)))
                .test(Branches.eq(SerialType.PARALLEL)
                        .then(() -> new ParallelTaskProcessor(recordId)))
                .get();
    }

}
