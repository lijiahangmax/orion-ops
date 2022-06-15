package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用发布审核请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/10 20:37
 */
@Data
@ApiModel(value = "应用发布审核请求")
public class ApplicationReleaseAuditRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * @see com.orion.ops.consts.AuditStatus
     */
    @ApiModelProperty(value = "状态 10通过 20驳回")
    private Integer status;

    @ApiModelProperty(value = "描述")
    private String reason;

}
