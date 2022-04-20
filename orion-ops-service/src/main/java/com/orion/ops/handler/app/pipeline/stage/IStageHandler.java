package com.orion.ops.handler.app.pipeline.stage;

import com.orion.able.Executable;
import com.orion.ops.consts.app.PipelineDetailStatus;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.entity.domain.ApplicationPipelineDetailRecordDO;
import com.orion.ops.entity.domain.ApplicationPipelineRecordDO;
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
     * @param record record
     * @param detail detail
     * @return 阶段处理器
     */
    static IStageHandler with(ApplicationPipelineRecordDO record, ApplicationPipelineDetailRecordDO detail) {
        StageType stageType = StageType.of(detail.getStageType());
        switch (stageType) {
            case BUILD:
                return new BuildStageHandler(record, detail);
            case RELEASE:
                return new ReleaseStageHandler(record, detail);
            default:
                throw Exceptions.argument();
        }
    }

}
