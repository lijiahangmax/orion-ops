package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.ApplicationBuildStatisticsDTO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 构建统计响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 16:03
 */
@Data
@ApiModel(value = "构建统计响应")
public class ApplicationBuildStatisticsMetricsVO {

    @ApiModelProperty(value = "构建次数")
    private Integer buildCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
    private Integer failureCount;

    @ApiModelProperty(value = "成功平均构建时长毫秒")
    private Long avgUsed;

    @ApiModelProperty(value = "成功平均构建时长")
    private String avgUsedInterval;

    static {
        TypeStore.STORE.register(ApplicationBuildStatisticsDTO.class, ApplicationBuildStatisticsMetricsVO.class, p -> {
            ApplicationBuildStatisticsMetricsVO vo = new ApplicationBuildStatisticsMetricsVO();
            vo.setBuildCount(p.getBuildCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            vo.setAvgUsed(p.getAvgUsed());
            vo.setAvgUsedInterval(Utils.interval(p.getAvgUsed()));
            return vo;
        });
    }

}
