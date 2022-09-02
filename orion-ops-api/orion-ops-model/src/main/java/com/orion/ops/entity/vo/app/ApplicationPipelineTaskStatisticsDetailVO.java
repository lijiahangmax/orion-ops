package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用流水线统计分析操作日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 15:05
 */
@Data
@ApiModel(value = "应用流水线统计分析操作日志响应")
@SuppressWarnings("ALL")
public class ApplicationPipelineTaskStatisticsDetailVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "操作id")
    private Long detailId;

    @ApiModelProperty(value = "引用id")
    private Long relId;

    /**
     * @see com.orion.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    private Integer stageType;

    /**
     * @see com.orion.ops.constant.app.PipelineDetailStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止")
    private Integer status;

    @ApiModelProperty(value = "操作时长 (任何状态)")
    private Long used;

    @ApiModelProperty(value = "操作时长 (任何状态)")
    private String usedInterval;

}
