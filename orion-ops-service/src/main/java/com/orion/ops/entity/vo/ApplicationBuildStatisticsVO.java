package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.ApplicationBuildStatisticsDTO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.List;

/**
 * 构建统计
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 16:03
 */
@Data
public class ApplicationBuildStatisticsVO {

    /**
     * 构建次数
     */
    private Integer buildCount;

    /**
     * 成功次数
     */
    private Integer successCount;

    /**
     * 失败次数
     */
    private Integer failureCount;

    /**
     * 平均构建时长ms (成功)
     */
    private Long avgUsed;

    /**
     * 平均构建时长 (成功)
     */
    private String avgUsedInterval;

    /**
     * 构建统计解析
     */
    private ApplicationBuildStatisticsAnalysisVO analysis;

    /**
     * 构建图表
     */
    private List<ApplicationBuildStatisticsChartVO> charts;

    static {
        TypeStore.STORE.register(ApplicationBuildStatisticsDTO.class, ApplicationBuildStatisticsVO.class, p -> {
            ApplicationBuildStatisticsVO vo = new ApplicationBuildStatisticsVO();
            vo.setBuildCount(p.getBuildCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            vo.setAvgUsed(p.getAvgUsed());
            vo.setAvgUsedInterval(Utils.interval(p.getAvgUsed()));
            return vo;
        });
    }

}
