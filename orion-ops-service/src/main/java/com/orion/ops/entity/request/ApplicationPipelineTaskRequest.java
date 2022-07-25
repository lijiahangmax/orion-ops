package com.orion.ops.entity.request;

import com.orion.lang.define.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 流水线明细请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 10:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "流水线明细请求")
public class ApplicationPipelineTaskRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    @ApiModelProperty(value = "detailId")
    private Long detailId;

    @ApiModelProperty(value = "detailId")
    private List<Long> detailIdList;

    @ApiModelProperty(value = "流水线id")
    private Long pipelineId;

    @ApiModelProperty(value = "流水线名称")
    private String pipelineName;

    @ApiModelProperty(value = "执行标题")
    private String title;

    @ApiModelProperty(value = "执行描述")
    private String description;

    /**
     * @see com.orion.ops.constant.app.TimedType
     */
    @ApiModelProperty(value = "是否是定时执行 10普通执行 20定时执行")
    private Integer timedExec;

    @ApiModelProperty(value = "定时执行时间")
    private Date timedExecTime;

    /**
     * @see com.orion.ops.constant.app.PipelineStatus
     */
    @ApiModelProperty(value = "执行状态 10待审核 20审核驳回 30待执行 35待调度 40执行中 50执行完成 60执行停止 70执行失败")
    private Integer status;

    /**
     * @see com.orion.ops.constant.AuditStatus
     */
    @ApiModelProperty(value = "审核状态 10通过 20驳回")
    private Integer auditStatus;

    @ApiModelProperty(value = "审核描述")
    private String auditReason;

    @ApiModelProperty(value = "执行明细")
    private List<ApplicationPipelineTaskDetailRequest> details;

}
