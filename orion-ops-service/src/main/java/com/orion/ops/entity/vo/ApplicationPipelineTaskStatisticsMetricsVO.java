package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.ApplicationPipelineTaskStatisticsDTO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 流水线执行统计指标
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 11:04
 */
@Data
public class ApplicationPipelineTaskStatisticsMetricsVO {

    /**
     * 执行次数
     */
    private Integer execCount;

    /**
     * 成功次数
     */
    private Integer successCount;

    /**
     * 失败次数
     */
    private Integer failureCount;

    /**
     * 平均发布时长ms (成功)
     */
    private Long avgUsed;

    /**
     * 平均发布时长 (成功)
     */
    private String avgUsedInterval;

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskStatisticsDTO.class, ApplicationPipelineTaskStatisticsMetricsVO.class, p -> {
            ApplicationPipelineTaskStatisticsMetricsVO vo = new ApplicationPipelineTaskStatisticsMetricsVO();
            vo.setExecCount(p.getExecCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            vo.setAvgUsed(p.getAvgUsed());
            vo.setAvgUsedInterval(Utils.interval(p.getAvgUsed()));
            return vo;
        });
    }

}
