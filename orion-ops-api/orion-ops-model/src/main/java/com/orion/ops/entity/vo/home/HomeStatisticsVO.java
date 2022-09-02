package com.orion.ops.entity.vo.home;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 首页统计响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/20 14:07
 */
@Data
@ApiModel(value = "首页统计响应")
public class HomeStatisticsVO {

    @ApiModelProperty(value = "数量统计")
    private HomeStatisticsCountVO count;

}
