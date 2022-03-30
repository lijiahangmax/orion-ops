package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.SchedulerTaskRecordStatisticsDTO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

/**
 * 调度任务统计图标
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/21 11:09
 */
@Data
public class SchedulerTaskRecordStatisticsChartVO {

    /**
     * 日期
     */
    private String date;

    /**
     * 调度次数
     */
    private Integer scheduledCount;

    /**
     * 成功次数
     */
    private Integer successCount;

    /**
     * 失败次数
     */
    private Integer failureCount;

    static {
        TypeStore.STORE.register(SchedulerTaskRecordStatisticsDTO.class, SchedulerTaskRecordStatisticsChartVO.class, p -> {
            SchedulerTaskRecordStatisticsChartVO vo = new SchedulerTaskRecordStatisticsChartVO();
            vo.setDate(Dates.format(p.getDate(), Dates.YMD));
            vo.setScheduledCount(p.getScheduledCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            return vo;
        });
    }

}
