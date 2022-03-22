package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.SchedulerTaskMachineRecordDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 调度机器执行状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/25 17:23
 */
@Data
public class SchedulerTaskMachineRecordStatusVO {

    /**
     * id
     */
    private Long id;

    /**
     * 执行记录id
     */
    private Long recordId;

    /**
     * @see com.orion.ops.consts.app.ActionStatus
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

    /**
     * exitCode
     */
    private Integer exitCode;

    static {
        TypeStore.STORE.register(SchedulerTaskMachineRecordDO.class, SchedulerTaskMachineRecordStatusVO.class, p -> {
            SchedulerTaskMachineRecordStatusVO vo = new SchedulerTaskMachineRecordStatusVO();
            vo.setId(p.getId());
            vo.setRecordId(p.getRecordId());
            vo.setStatus(p.getExecStatus());
            vo.setExitCode(p.getExitCode());
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
