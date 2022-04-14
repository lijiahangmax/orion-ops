package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
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
@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationPipelineRecordRequest extends PageRequest {

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private List<Long> idList;

    /**
     * 流水线id
     */
    private Long pipelineId;

    /**
     * 流水线名称
     */
    private String pipelineName;

    /**
     * 执行标题
     */
    private String title;

    /**
     * 执行描述
     */
    private String description;

    /**
     * 是否是定时执行 10普通执行 20定时执行
     *
     * @see com.orion.ops.consts.app.TimedType
     */
    private Integer timedExec;

    /**
     * 定时执行时间
     */
    private Date timedExecTime;

    /**
     * 执行状态 10待审核 20审核驳回 30待执行 35待调度 40执行中 50执行完成 60执行停止 70执行失败
     *
     * @see com.orion.ops.consts.app.PipelineStatus
     */
    private Integer status;

    /**
     * 审核状态 10通过 20驳回
     *
     * @see com.orion.ops.consts.AuditStatus
     */
    private Integer auditStatus;

    /**
     * 审核描述
     */
    private String auditReason;

    /**
     * 执行明细
     */
    private List<ApplicationPipelineDetailRecordRequest> details;

}
