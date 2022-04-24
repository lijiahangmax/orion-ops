package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 应用流水线任务详情状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/24 16:53
 */
@Data
public class ApplicationPipelineTaskDetailStatusVO {
    /**
     * id
     */
    private Long id;

    /**
     * 流水线任务id
     */
    private Long taskId;

    /**
     * relId
     */
    private Long relId;

    /**
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止
     *
     * @see com.orion.ops.consts.app.PipelineDetailStatus
     */
    private Integer status;

    /**
     * 执行开始时间
     */
    private Date startTime;

    /**
     * 执行开始时间
     */
    private String startTimeAgo;

    /**
     * 执行结束时间
     */
    private Date endTime;

    /**
     * 执行结束时间
     */
    private String endTimeAgo;

    /**
     * 使用时间 ms
     */
    private Long used;

    /**
     * 使用时间
     */
    private String keepTime;

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDetailDO.class, ApplicationPipelineTaskDetailStatusVO.class, p -> {
            ApplicationPipelineTaskDetailStatusVO vo = new ApplicationPipelineTaskDetailStatusVO();
            vo.setId(p.getId());
            vo.setTaskId(p.getTaskId());
            vo.setRelId(p.getRelId());
            vo.setStatus(p.getExecStatus());
            Date startTime = p.getExecStartTime();
            Date endTime = p.getExecEndTime();
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            Optional.ofNullable(startTime).map(Dates::ago).ifPresent(vo::setStartTimeAgo);
            Optional.ofNullable(endTime).map(Dates::ago).ifPresent(vo::setEndTimeAgo);
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

}
