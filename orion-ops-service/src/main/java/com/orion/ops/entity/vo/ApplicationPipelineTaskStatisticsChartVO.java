package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.dto.ApplicationPipelineTaskStatisticsDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 流水线统计表格响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 11:08
 */
@Data
@ApiModel(value = "流水线统计表格响应")
public class ApplicationPipelineTaskStatisticsChartVO {

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "执行次数")
    private Integer execCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
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
