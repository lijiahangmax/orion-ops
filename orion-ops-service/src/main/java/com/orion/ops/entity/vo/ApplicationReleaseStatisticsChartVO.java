package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.dto.ApplicationReleaseStatisticsDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发布统计表格响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 16:42
 */
@Data
@ApiModel(value = "发布统计表格响应")
public class ApplicationReleaseStatisticsChartVO {

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "发布次数")
    private Integer releaseCount;

    @ApiModelProperty(value = "成功次数")
    private Integer successCount;

    @ApiModelProperty(value = "失败次数")
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
