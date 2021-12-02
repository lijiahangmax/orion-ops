package com.orion.ops.entity.request;

import lombok.Data;

import java.util.List;

/**
 * app配置同步请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 18:50
 */
@Data
public class ApplicationSyncConfigRequest {

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 需要同步到的环境id
     */
    private List<Long> targetProfileIdList;

}
