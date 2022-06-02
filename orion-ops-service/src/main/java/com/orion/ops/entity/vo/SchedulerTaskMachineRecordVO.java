package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.SchedulerTaskMachineRecordDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 调度机器执行明细
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/25 14:42
 */
@Data
public class SchedulerTaskMachineRecordVO {

    /**
     * id
     */
    private Long id;

    /**
     * 明细id
     */
    private Long recordId;

    /**
     * 执行机器id
     */
    private Long machineId;

    /**
     * 机器名称
     */
    private String machineName;

    /**
     * 机器主机
     */
    private String machineHost;

    /**
     * 机器唯一标识
     */
    private String machineTag;

    /**
     * 执行命令
     */
    private String command;

    /**
     * 执行状态 10待调度 20调度中 30调度成功 40调度失败 50已跳过 60已停止
     *
     * @see com.orion.ops.consts.scheduler.SchedulerTaskMachineStatus
     */
    private Integer status;

    /**
     * 退出码
     */
    private Integer exitCode;

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

    static {
        TypeStore.STORE.register(SchedulerTaskMachineRecordDO.class, SchedulerTaskMachineRecordVO.class, p -> {
            SchedulerTaskMachineRecordVO vo = new SchedulerTaskMachineRecordVO();
            vo.setId(p.getId());
            vo.setRecordId(p.getRecordId());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineHost(p.getMachineHost());
            vo.setMachineTag(p.getMachineTag());
            vo.setCommand(p.getExecCommand());
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
