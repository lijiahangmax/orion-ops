package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.dto.ApplicationReleaseStatisticsDTO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发布统计响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/30 17:17
 */
@Data
@ApiModel(value = "发布统计响应")
public class ApplicationReleaseStatisticsMetricsVO {

    @ApiModelProperty(value = "发布次数")
    private Integer releaseCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
    private Integer failureCount;

    @ApiModelProperty(value = "成功平均发布时长毫秒")
    private Long avgUsed;

    @ApiModelProperty(value = "成功平均发布时长")
    private String avgUsedInterval;

    static {
        TypeStore.STORE.register(ApplicationReleaseStatisticsDTO.class, ApplicationReleaseStatisticsMetricsVO.class, p -> {
            ApplicationReleaseStatisticsMetricsVO vo = new ApplicationReleaseStatisticsMetricsVO();
            vo.setReleaseCount(p.getReleaseCount());
            vo.setSuccessCount(p.getSuccessCount());
            vo.setFailureCount(p.getFailureCount());
            vo.setAvgUsed(p.getAvgUsed());
            vo.setAvgUsedInterval(Utils.interval(p.getAvgUsed()));
            return vo;
        });
    }

}
