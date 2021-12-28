package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 应用发布审核
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/10 20:37
 */
@Data
public class ApplicationReleaseAuditRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 状态 10通过 20驳回
     *
     * @see com.orion.ops.consts.AuditStatus
     */
    private Integer status;

    /**
     * 描述
     */
    private String reason;

}
