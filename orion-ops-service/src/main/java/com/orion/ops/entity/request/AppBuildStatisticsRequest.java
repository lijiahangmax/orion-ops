package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 应用构建统计
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 17:02
 */
@Data
public class AppBuildStatisticsRequest {

    /**
     * appId
     */
    private Long appId;

    /**
     * profileId
     */
    private Long profileId;

}
