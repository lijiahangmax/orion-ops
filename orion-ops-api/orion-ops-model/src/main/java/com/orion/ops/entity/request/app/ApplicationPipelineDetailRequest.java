package com.orion.ops.entity.request.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用流水线详情请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:25
 */
@Data
@ApiModel(value = "应用流水线详情请求")
@SuppressWarnings("ALL")
public class ApplicationPipelineDetailRequest {

    @ApiModelProperty(value = "应用id")
    private Long appId;

    /**
     * @see com.orion.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    private Integer stageType;

}
