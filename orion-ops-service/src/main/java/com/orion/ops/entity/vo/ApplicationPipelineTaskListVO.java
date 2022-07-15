package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 流水线明细详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/8 10:37
 */
@Data
@ApiModel(value = "流水线明细详情响应")
public class ApplicationPipelineTaskListVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "流水线id")
    private Long pipelineId;

    @ApiModelProperty(value = "流水线名称")
    private String pipelineName;

    @ApiModelProperty(value = "执行标题")
    private String title;

    @ApiModelProperty(value = "执行描述")
    private String description;

    /**
     * @see com.orion.ops.constant.app.PipelineStatus
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

    @ApiModelProperty(value = "执行人id")
    private Long execUserId;

    @ApiModelProperty(value = "执行人名称")
    private String execUserName;

    @ApiModelProperty(value = "执行开始时间")
    private Date execStartTime;

    @ApiModelProperty(value = "执行结束时间")
    private Date execEndTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDO.class, ApplicationPipelineTaskListVO.class, p -> {
            ApplicationPipelineTaskListVO vo = new ApplicationPipelineTaskListVO();
            vo.setId(p.getId());
            vo.setPipelineId(p.getPipelineId());
            vo.setPipelineName(p.getPipelineName());
            vo.setTitle(p.getExecTitle());
            vo.setDescription(p.getExecDescription());
            vo.setStatus(p.getExecStatus());
            vo.setTimedExec(p.getTimedExec());
            vo.setTimedExecTime(p.getTimedExecTime());
            vo.setCreateUserId(p.getCreateUserId());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setExecUserId(p.getExecUserId());
            vo.setExecUserName(p.getExecUserName());
            vo.setCreateTime(p.getCreateTime());
            Date startTime = p.getExecStartTime();
            Date endTime = p.getExecEndTime();
            vo.setExecStartTime(startTime);
            vo.setExecEndTime(endTime);
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

}
