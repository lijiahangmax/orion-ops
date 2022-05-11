package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;

/**
 * 应用流水线统计分析操作日志
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 15:05
 */
@Data
public class ApplicationPipelineTaskStatisticsDetailVO {

    /**
     * id
     */
    private Long id;

    /**
     * 操作id
     */
    private Long detailId;

    /**
     * 引用id
     */
    private Long relId;

    /**
     * 阶段类型 10构建 20发布
     *
     * @see com.orion.ops.consts.app.StageType
     */
    private Integer stageType;

    /**
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止
     *
     * @see com.orion.ops.consts.app.PipelineDetailStatus
     */
    private Integer status;

    /**
     * 操作时长 (任何状态)
     */
    private Long used;

    /**
     * 操作时长 (任何状态)
     */
    private String usedInterval;

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDetailDO.class, ApplicationPipelineTaskStatisticsDetailVO.class, p -> {
            ApplicationPipelineTaskStatisticsDetailVO vo = new ApplicationPipelineTaskStatisticsDetailVO();
            vo.setId(p.getId());
            vo.setRelId(p.getRelId());
            vo.setStageType(p.getStageType());
            vo.setStatus(p.getExecStatus());
            Date startTime = p.getExecStartTime();
            Date endTime = p.getExecEndTime();
            if (startTime != null && endTime != null) {
                long used = endTime.getTime() - startTime.getTime();
                vo.setUsed(used);
                vo.setUsedInterval(Utils.interval(used));
            }
            return vo;
        });
    }

}
