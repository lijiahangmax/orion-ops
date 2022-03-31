package com.orion.ops.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * 应用构建统计 DTO
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 15:35
 */
@Data
public class ApplicationBuildStatisticsDTO {

    /**
     * 构建次数
     */
    private Integer buildCount;

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
     * 平均构建时长ms (成功)
     */
    private Long avgUsed;

}
