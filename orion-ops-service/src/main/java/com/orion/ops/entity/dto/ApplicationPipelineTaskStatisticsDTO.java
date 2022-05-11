package com.orion.ops.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * 应用流水线统计 DTO
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 10:49
 */
@Data
public class ApplicationPipelineTaskStatisticsDTO {

    /**
     * 执行次数
     */
    private Integer execCount;

    /**
     * 成功次数
     */
    private Integer successCount;

    /**
     * 失败次数
     */
    private Integer failureCount;

    /**
     * 日期
     */
    private Date date;

    /**
     * 平均执行时长ms (成功)
     */
    private Long avgUsed;

}
