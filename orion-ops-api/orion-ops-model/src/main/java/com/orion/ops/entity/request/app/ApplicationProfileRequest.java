package com.orion.ops.entity.request.app;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用环境配置请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 18:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用环境配置请求")
@SuppressWarnings("ALL")
public class ApplicationProfileRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "唯一标识")
    private String tag;

    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "发布是否需要审核 1需要 2无需")
    private Integer releaseAudit;

}
