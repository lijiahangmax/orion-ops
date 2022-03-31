package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.ApplicationBuildStatisticsDTO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

/**
 * 构建统计表格
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 16:42
 */
@Data
public class ApplicationBuildStatisticsChartVO {

    /**
     * 日期
     */
    private String date;

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

    static {
        TypeStore.STORE.register(ApplicationBuildStatisticsDTO.class, ApplicationBuildStatisticsChartVO.class, p -> {
            ApplicationBuildStatisticsChartVO vo = new ApplicationBuildStatisticsChartVO();
            vo.setDate(Dates.format(p.getDate(), Dates.YMD));
            vo.setBuildCount(p.getBuildCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            return vo;
        });
    }

}
