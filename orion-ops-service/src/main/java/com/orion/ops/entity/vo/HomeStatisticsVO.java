package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * 首页统计返回
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/20 14:07
 */
@Data
public class HomeStatisticsVO {

    /**
     * 数量统计
     */
    private HomeStatisticsCountVO count;

}
