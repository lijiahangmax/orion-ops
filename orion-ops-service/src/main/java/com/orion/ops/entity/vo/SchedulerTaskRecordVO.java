package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.SchedulerTaskRecordDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 调度任务明细
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/25 14:55
 */
@Data
public class SchedulerTaskRecordVO {

    /**
     * id
     */
    private Long id;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务状态 10待调度 20调度中 30调度成功 40调度失败 50已停止
     *
     * @see com.orion.ops.consts.scheduler.SchedulerTaskStatus
     */
    private Integer status;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 开始时间
     */
    private String startTimeAgo;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 结束时间
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

    /**
     * 机器
     */
    private List<SchedulerTaskMachineRecordVO> machines;

    static {
        TypeStore.STORE.register(SchedulerTaskRecordDO.class, SchedulerTaskRecordVO.class, p -> {
            SchedulerTaskRecordVO vo = new SchedulerTaskRecordVO();
            vo.setId(p.getId());
            vo.setTaskId(p.getTaskId());
            vo.setTaskName(p.getTaskName());
            vo.setStatus(p.getTaskStatus());
            Date startTime = p.getStartTime();
            Date endTime = p.getEndTime();
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Dates.interval(vo.getUsed(), false, "d", "h", "m", "s"));
            }
            Optional.ofNullable(startTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setStartTimeAgo);
            Optional.ofNullable(endTime)
                    .map(Dates::ago)
                    .ifPresent(vo::setEndTimeAgo);
            return vo;
        });
    }

}
