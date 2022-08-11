package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 应用发布明细响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/31 8:46
 */
@Data
@ApiModel(value = "应用发布明细响应")
@SuppressWarnings("ALL")
public class ApplicationReleaseStatisticsRecordVO {

    @ApiModelProperty(value = "发布id")
    private Long releaseId;

    @ApiModelProperty(value = "发布标题")
    private String releaseTitle;

    @ApiModelProperty(value = "发布时间")
    private Date releaseDate;

    /**
     * @see com.orion.ops.constant.app.ReleaseStatus
     */
    @ApiModelProperty(value = "发布状态 10待审核 20审核驳回 30待发布 35待调度 40发布中 50发布完成 60发布停止 70发布失败")
    private Integer status;

    @ApiModelProperty(value = "成功构建操作时长毫秒")
    private Long used;

    @ApiModelProperty(value = "成功构建操作时长")
    private String usedInterval;

    @ApiModelProperty(value = "发布机器")
    private List<ApplicationReleaseStatisticsMachineVO> machines;

}
