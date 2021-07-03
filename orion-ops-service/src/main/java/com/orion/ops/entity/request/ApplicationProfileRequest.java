package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 应用环境配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 18:03
 */
@Data
public class ApplicationProfileRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * tag
     */
    private String tag;

    /**
     * 描述
     */
    private String description;

    /**
     * 发布是否需要审核 1需要 2无需
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    private Integer releaseAudit;

}
