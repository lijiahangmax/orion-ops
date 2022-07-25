package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.SchedulerTaskRecordDO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 调度任务明细响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/25 14:55
 */
@Data
@ApiModel(value = "调度任务明细响应")
public class SchedulerTaskRecordVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "任务id")
    private Long taskId;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    /**
     * @see com.orion.ops.constant.scheduler.SchedulerTaskStatus
     */
    @ApiModelProperty(value = "任务状态 10待调度 20调度中 30调度成功 40调度失败 50已停止")
    private Integer status;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "调度机器")
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
                vo.setKeepTime(Utils.interval(vo.getUsed()));
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
