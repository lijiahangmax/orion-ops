package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationPipelineTaskDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;

/**
 * 流水线明细详情
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/8 10:37
 */
@Data
public class ApplicationPipelineTaskListVO {

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
     * 执行结束时间
     */
    private Date execEndTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 使用时间 ms
     */
    private Long used;

    /**
     * 使用时间
     */
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
