package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationPipelineTaskDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 流水线明细详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/14 9:47
 */
@Data
@ApiModel(value = "流水线明细详情响应")
public class ApplicationPipelineTaskVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "流水线id")
    private Long pipelineId;

    @ApiModelProperty(value = "流水线名称")
    private String pipelineName;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    @ApiModelProperty(value = "环境名称")
    private String profileName;

    @ApiModelProperty(value = "环境唯一标识")
    private String profileTag;

    @ApiModelProperty(value = "执行标题")
    private String title;

    @ApiModelProperty(value = "执行描述")
    private String description;

    /**
     * @see com.orion.ops.consts.app.PipelineStatus
     */
    @ApiModelProperty(value = "执行状态 10待审核 20审核驳回 30待执行 35待调度 40执行中 50执行完成 60执行停止 70执行失败")
    private Integer status;

    @ApiModelProperty(value = "是否是定时执行 10普通执行 20定时执行")
    private Integer timedExec;

    @ApiModelProperty(value = "定时执行时间")
    private Date timedExecTime;

    @ApiModelProperty(value = "创建人id")
    private Long createUserId;

    @ApiModelProperty(value = "创建人名称")
    private String createUserName;

    @ApiModelProperty(value = "审核人id")
    private Long auditUserId;

    @ApiModelProperty(value = "审核人名称")
    private String auditUserName;

    @ApiModelProperty(value = "审核时间")
    private Date auditTime;

    @ApiModelProperty(value = "审核时间")
    private String auditTimeAgo;

    @ApiModelProperty(value = "审核备注")
    private String auditReason;

    @ApiModelProperty(value = "执行人id")
    private Long execUserId;

    @ApiModelProperty(value = "执行人名称")
    private String execUserName;

    @ApiModelProperty(value = "执行开始时间")
    private Date execStartTime;

    @ApiModelProperty(value = "执行开始时间")
    private String execStartTimeAgo;

    @ApiModelProperty(value = "执行结束时间")
    private Date execEndTime;

    @ApiModelProperty(value = "执行结束时间")
    private String execEndTimeAgo;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
    private String createTimeAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "详情")
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
