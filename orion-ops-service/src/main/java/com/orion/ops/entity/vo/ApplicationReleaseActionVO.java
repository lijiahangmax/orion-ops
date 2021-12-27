package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationReleaseActionDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 发布操作
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/20 14:22
 */
@Data
public class ApplicationReleaseActionVO {

    /**
     * id
     */
    private Long id;

    /**
     * 发布机器id
     */
    private Long releaseMachineId;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 上线单id
     */
    private Long releaseId;

    /**
     * 操作id
     */
    private Long actionId;

    /**
     * 操作名称
     */
    private String name;

    /**
     * 操作类型
     *
     * @see com.orion.ops.consts.app.ActionType
     */
    private Integer type;

    /**
     * 操作命令
     */
    private String command;

    /**
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消
     *
     * @see com.orion.ops.consts.app.ActionStatus
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

    static {
        TypeStore.STORE.register(ApplicationReleaseActionDO.class, ApplicationReleaseActionVO.class, p -> {
            ApplicationReleaseActionVO vo = new ApplicationReleaseActionVO();
            vo.setId(p.getId());
            vo.setReleaseMachineId(p.getReleaseMachineId());
            vo.setMachineId(p.getMachineId());
            vo.setReleaseId(p.getReleaseId());
            vo.setActionId(p.getActionId());
            vo.setName(p.getActionName());
            vo.setType(p.getActionType());
            vo.setCommand(p.getActionCommand());
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
