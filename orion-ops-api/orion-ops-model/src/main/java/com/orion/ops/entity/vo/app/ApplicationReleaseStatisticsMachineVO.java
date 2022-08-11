package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应用发布统计机器响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/31 9:39
 */
@Data
@ApiModel(value = "应用发布统计机器响应")
@SuppressWarnings("ALL")
public class ApplicationReleaseStatisticsMachineVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    /**
     * @see com.orion.ops.constant.app.ActionStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消")
    private Integer status;

    @ApiModelProperty(value = "机器操作时长毫秒")
    private Long used;

    @ApiModelProperty(value = "机器操作时长")
    private String usedInterval;

    @ApiModelProperty(value = "发布操作")
    private List<ApplicationActionLogStatisticsVO> actionLogs;

}
