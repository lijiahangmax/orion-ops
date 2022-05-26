package com.orion.ops.entity.dto;

import lombok.Data;

/**
 * 首页统计数量缓存
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/20 14:24
 */
@Data
public class StatisticsCountDTO {

    /**
     * 机器数量
     */
    private Integer machineCount;

    /**
     * 环境数量
     */
    private Integer profileCount;

    /**
     * 应用数量
     */
    private Integer appCount;

    /**
     * 流水线数量
     */
    private Integer pipelineCount;

}
