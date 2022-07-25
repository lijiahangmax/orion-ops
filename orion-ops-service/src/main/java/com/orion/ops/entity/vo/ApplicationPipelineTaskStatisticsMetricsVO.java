package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.dto.ApplicationPipelineTaskStatisticsDTO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 流水线执行统计指标响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 11:04
 */
@Data
@ApiModel(value = "流水线执行统计指标响应")
public class ApplicationPipelineTaskStatisticsMetricsVO {

    @ApiModelProperty(value = "执行次数")
    private Integer execCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
    private Integer failureCount;

    @ApiModelProperty(value = "成功平均发布时长毫秒")
    private Long avgUsed;

    @ApiModelProperty(value = "成功平均发布时长")
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
