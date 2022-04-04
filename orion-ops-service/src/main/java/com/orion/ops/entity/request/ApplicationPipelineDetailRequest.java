package com.orion.ops.entity.request;

import lombok.Data;

/**
 * 应用流水线详情请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:25
 */
@Data
public class ApplicationPipelineDetailRequest {

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 阶段类型 10构建 20发布
     *
     * @see com.orion.ops.consts.app.StageType
     */
    private Integer stageType;

}
