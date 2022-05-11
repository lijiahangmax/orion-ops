package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.ApplicationPipelineTaskStatisticsDTO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

/**
 * 流水线统计表格
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 11:08
 */
@Data
public class ApplicationPipelineTaskStatisticsChartVO {

    /**
     * 日期
     */
    private String date;

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

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskStatisticsDTO.class, ApplicationPipelineTaskStatisticsChartVO.class, p -> {
            ApplicationPipelineTaskStatisticsChartVO vo = new ApplicationPipelineTaskStatisticsChartVO();
            vo.setDate(Dates.format(p.getDate(), Dates.YMD));
            vo.setExecCount(p.getExecCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            return vo;
        });
    }

}
