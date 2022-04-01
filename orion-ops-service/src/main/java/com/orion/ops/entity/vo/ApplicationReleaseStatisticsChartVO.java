package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.ApplicationReleaseStatisticsDTO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

/**
 * 发布统计表格
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 16:42
 */
@Data
public class ApplicationReleaseStatisticsChartVO {

    /**
     * 日期
     */
    private String date;

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

    static {
        TypeStore.STORE.register(ApplicationReleaseStatisticsDTO.class, ApplicationReleaseStatisticsChartVO.class, p -> {
            ApplicationReleaseStatisticsChartVO vo = new ApplicationReleaseStatisticsChartVO();
            vo.setDate(Dates.format(p.getDate(), Dates.YMD));
            vo.setReleaseCount(p.getReleaseCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            return vo;
        });
    }

}
