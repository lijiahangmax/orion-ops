package com.orion.ops.handler.app.pipeline.stage;

import com.orion.able.Executable;
import com.orion.ops.consts.app.PipelineDetailStatus;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDO;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import com.orion.utils.Exceptions;

/**
 * 流水线阶段处理器接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/15 14:56
 */
public interface IStageHandler extends Executable {

    /**
     * 停止执行
     */
    void terminate();

    /**
     * 跳过执行
     */
    void skip();

    /**
     * 获取状态
     *
     * @return status
     */
    PipelineDetailStatus getStatus();

    /**
     * 获取阶段处理器
     *
     * @param task   task
     * @param detail detail
     * @return 阶段处理器
     */
    static IStageHandler with(ApplicationPipelineTaskDO task, ApplicationPipelineTaskDetailDO detail) {
        StageType stageType = StageType.of(detail.getStageType());
        switch (stageType) {
            case BUILD:
                return new BuildStageHandler(task, detail);
            case RELEASE:
                return new ReleaseStageHandler(task, detail);
            default:
                throw Exceptions.argument();
        }
    }

}
