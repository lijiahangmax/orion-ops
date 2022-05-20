package com.orion.ops.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 应用流水线统计分析操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 11:13
 */
@Data
public class ApplicationPipelineTaskStatisticsViewVO {

    /**
     * 平均执行时长ms (成功)
     */
    private Long avgUsed;

    /**
     * 平均执行时长 (成功)
     */
    private String avgUsedInterval;

    /**
     * 流水线操作
     */
    private List<ApplicationPipelineStatisticsDetailVO> details;

    /**
     * 流水线执行记录
     */
    private List<ApplicationPipelineTaskStatisticsTaskVO> pipelineTaskList;

}
