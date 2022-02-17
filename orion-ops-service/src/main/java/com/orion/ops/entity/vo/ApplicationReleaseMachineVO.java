package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/20 14:15
 */
@Data
public class ApplicationReleaseMachineVO {

    /**
     * id
     */
    private Long id;

    /**
     * 上线单id
     */
    private Long releaseId;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器名称
     */
    private String machineName;

    /**
     * 机器tag
     */
    private String machineTag;

    /**
     * 机器主机
     */
    private String machineHost;

    /**
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消
     *
     * @see com.orion.ops.consts.app.ActionType
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
     * 操作
     */
    private List<ApplicationActionLogVO> actions;

    static {
        TypeStore.STORE.register(ApplicationReleaseMachineDO.class, ApplicationReleaseMachineVO.class, p -> {
            ApplicationReleaseMachineVO vo = new ApplicationReleaseMachineVO();
            vo.setId(p.getId());
            vo.setReleaseId(p.getReleaseId());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineTag(p.getMachineTag());
            vo.setMachineHost(p.getMachineHost());
            vo.setStatus(p.getRunStatus());
            Date startTime = p.getStartTime();
            Date endTime = p.getEndTime();
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            vo.setStartTimeAgo(Optional.ofNullable(startTime).map(Dates::ago).orElse(null));
            vo.setEndTimeAgo(Optional.ofNullable(endTime).map(Dates::ago).orElse(null));
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Dates.interval(vo.getUsed(), false, "d", "h", "m", "s"));
            }
            return vo;
        });
    }

}
