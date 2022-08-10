package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用构建统计分析操作日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 18:07
 */
@Data
@ApiModel(value = "应用构建统计分析操作日志响应")
@SuppressWarnings("ALL")
public class ApplicationActionLogStatisticsVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "操作id")
    private Long actionId;

    /**
     * @see com.orion.ops.constant.app.ActionStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消")
    private Integer status;

    @ApiModelProperty(value = "操作时长 (任何状态)")
    private Long used;

    @ApiModelProperty(value = "操作时长 (任何状态)")
    private String usedInterval;

}
