package com.orion.ops.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * 应用发布统计 DTO
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/30 17:19
 */
@Data
public class ApplicationReleaseStatisticsDTO {

    /**
     * 发布次数
     */
    private Integer releaseCount;

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
     * 平均发布时长ms (成功)
     */
    private Long avgUsed;

}
