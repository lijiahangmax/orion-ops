package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.SchedulerTaskMachineRecordDO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 调度机器执行明细响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/25 14:42
 */
@Data
@ApiModel(value = "调度机器执行明细响应")
public class SchedulerTaskMachineRecordVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "明细id")
    private Long recordId;

    @ApiModelProperty(value = "执行机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器主机")
    private String machineHost;

    @ApiModelProperty(value = "机器唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "执行命令")
    private String command;

    /**
     * @see com.orion.ops.constant.scheduler.SchedulerTaskMachineStatus
     */
    @ApiModelProperty(value = "执行状态 10待调度 20调度中 30调度成功 40调度失败 50已跳过 60已停止")
    private Integer status;

    @ApiModelProperty(value = "退出码")
    private Integer exitCode;

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
