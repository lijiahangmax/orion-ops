package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用流水线详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 11:40
 */
@Data
@ApiModel(value = "应用流水线详情响应")
@SuppressWarnings("ALL")
public class ApplicationPipelineStatisticsDetailVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    /**
     * @see com.orion.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    private Integer stageType;

    @ApiModelProperty(value = "成功平均操作时长毫秒")
    private Long avgUsed;

    @ApiModelProperty(value = "成平均操作时长")
    private String avgUsedInterval;

}
