package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationPipelineTaskDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 流水线明细详情
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/14 9:47
 */
@Data
public class ApplicationPipelineTaskVO {

    /**
     * id
     */
    private Long id;

    /**
     * 流水线id
     */
    private Long pipelineId;

    /**
     * 流水线名称
     */
    private String pipelineName;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 环境名称
     */
    private String profileName;

    /**
     * 环境唯一标识
     */
    private String profileTag;

    /**
     * 执行标题
     */
    private String title;

    /**
     * 执行描述
     */
    private String description;

    /**
     * 执行状态 10待审核 20审核驳回 30待执行 35待调度 40执行中 50执行完成 60执行停止 70执行失败
     *
     * @see com.orion.ops.consts.app.PipelineStatus
     */
    private Integer status;

    /**
     * 是否是定时执行 10普通执行 20定时执行
     */
    private Integer timedExec;

    /**
     * 定时执行时间
     */
    private Date timedExecTime;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 审核人id
     */
    private Long auditUserId;

    /**
     * 审核人名称
     */
    private String auditUserName;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核时间
     */
    private String auditTimeAgo;

    /**
     * 审核备注
     */
    private String auditReason;

    /**
     * 执行人id
     */
    private Long execUserId;

    /**
     * 执行人名称
     */
    private String execUserName;

    /**
     * 执行开始时间
     */
    private Date execStartTime;

    /**
     * 执行开始时间
     */
    private String execStartTimeAgo;

    /**
     * 执行结束时间
     */
    private Date execEndTime;

    /**
     * 执行结束时间
     */
    private String execEndTimeAgo;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间
     */
    private String createTimeAgo;

    /**
     * 使用时间 ms
     */
    private Long used;

    /**
     * 使用时间
     */
    private String keepTime;

    /**
     * 详情
     */
    private List<ApplicationPipelineTaskDetailVO> details;

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDO.class, ApplicationPipelineTaskVO.class, p -> {
            ApplicationPipelineTaskVO vo = new ApplicationPipelineTaskVO();
            vo.setId(p.getId());
            vo.setPipelineId(p.getPipelineId());
            vo.setPipelineName(p.getPipelineName());
            vo.setProfileId(p.getProfileId());
            vo.setProfileName(p.getProfileName());
            vo.setProfileTag(p.getProfileTag());
            vo.setTitle(p.getExecTitle());
            vo.setDescription(p.getExecDescription());
            vo.setStatus(p.getExecStatus());
            vo.setTimedExec(p.getTimedExec());
            vo.setTimedExecTime(p.getTimedExecTime());
            vo.setCreateUserId(p.getCreateUserId());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setAuditUserId(p.getAuditUserId());
            vo.setAuditUserName(p.getAuditUserName());
            vo.setAuditTime(p.getAuditTime());
            Optional.ofNullable(p.getAuditTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setAuditTimeAgo);
            vo.setAuditReason(p.getAuditReason());
            vo.setExecUserId(p.getCreateUserId());
            vo.setExecUserName(p.getCreateUserName());
            vo.setCreateTime(p.getCreateTime());
            Optional.ofNullable(p.getCreateTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setCreateTimeAgo);
            Date startTime = p.getExecStartTime();
            Date endTime = p.getExecEndTime();
            vo.setExecStartTime(startTime);
            vo.setExecEndTime(endTime);
            Optional.ofNullable(startTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setExecStartTimeAgo);
            Optional.ofNullable(endTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setExecEndTimeAgo);
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

}
