package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 应用流水线统计分析操作日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 15:01
 */
@Data
@ApiModel(value = "应用流水线统计分析操作日志响应")
@SuppressWarnings("ALL")
public class ApplicationPipelineTaskStatisticsTaskVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "执行时间")
    private Date execDate;

    /**
     * @see com.orion.ops.constant.app.PipelineStatus
     */
    @ApiModelProperty(value = "执行状态 10待审核 20审核驳回 30待执行 35待调度 40执行中 50执行完成 60执行停止 70执行失败")
    private Integer status;

    @ApiModelProperty(value = "成功执行操作时长毫秒")
    private Long used;

    @ApiModelProperty(value = "成功执行操作时长")
    private String usedInterval;

    @ApiModelProperty(value = "执行操作")
    private List<ApplicationPipelineTaskStatisticsDetailVO> details;

}
