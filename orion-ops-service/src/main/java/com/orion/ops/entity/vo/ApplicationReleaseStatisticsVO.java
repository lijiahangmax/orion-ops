package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.ApplicationReleaseStatisticsDTO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.List;

/**
 * 发布统计
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/30 17:17
 */
@Data
public class ApplicationReleaseStatisticsVO {

    /**
     * 发布次数
     */
    private Integer releaseCount;

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

    /**
     * 统计
     */
    private ApplicationReleaseStatisticsAnalysisVO analysis;

    /**
     * 发布图表
     */
    private List<ApplicationReleaseStatisticsChartVO> charts;

    static {
        TypeStore.STORE.register(ApplicationReleaseStatisticsDTO.class, ApplicationReleaseStatisticsVO.class, p -> {
            ApplicationReleaseStatisticsVO vo = new ApplicationReleaseStatisticsVO();
            vo.setReleaseCount(p.getReleaseCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            vo.setAvgUsed(p.getAvgUsed());
            vo.setAvgUsedInterval(Utils.interval(p.getAvgUsed()));
            return vo;
        });
    }

}
