package com.orion.ops.entity.request;

import lombok.Data;

/**
 * app 版本控制请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 18:36
 */
@Data
public class ApplicationVcsRequest {

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 分支名称
     */
    private String branchName;

}
