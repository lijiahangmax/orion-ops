package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 应用发布统计
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/30 17:46
 */
@Data
public class AppReleaseStatisticsRequest {

    /**
     * appId
     */
    private Long appId;

    /**
     * profileId
     */
    private Long profileId;

}
