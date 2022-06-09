package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.SchedulerTaskRecordStatisticsDTO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 调度任务统计图表响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/21 11:09
 */
@Data
@ApiModel(value = "调度任务统计图表响应")
public class SchedulerTaskRecordStatisticsChartVO {

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "调度次数")
    private Integer scheduledCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
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
