package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 应用流水线统计
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 11:10
 */
@Data
public class AppPipelineTaskStatisticsRequest {

    /**
     * pipelineId
     */
    private Long pipelineId;

}
