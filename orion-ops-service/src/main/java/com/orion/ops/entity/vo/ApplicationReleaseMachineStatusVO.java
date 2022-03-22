package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 发布机器id
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/29 21:52
 */
@Data
public class ApplicationReleaseMachineStatusVO {

    /**
     * id
     */
    private Long id;

    /**
     * 状态
     *
     * @see com.orion.ops.consts.app.ActionStatus
     */
    private Integer status;

    /**
     * 发布开始时间
     */
    private Date startTime;

    /**
     * 发布开始时间
     */
    private String startTimeAgo;

    /**
     * 发布结束时间
     */
    private Date endTime;

    /**
     * 发布结束时间
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
     * action状态
     */
    private List<ApplicationActionStatusVO> actions;

    static {
        TypeStore.STORE.register(ApplicationReleaseMachineDO.class, ApplicationReleaseMachineStatusVO.class, p -> {
            ApplicationReleaseMachineStatusVO vo = new ApplicationReleaseMachineStatusVO();
            vo.setId(p.getId());
            vo.setStatus(p.getRunStatus());
            Date startTime = p.getStartTime(), endTime = p.getEndTime();
            vo.setStartTime(startTime);
            vo.setStartTimeAgo(Optional.ofNullable(startTime).map(Dates::ago).orElse(null));
            vo.setEndTime(endTime);
            vo.setEndTimeAgo(Optional.ofNullable(endTime).map(Dates::ago).orElse(null));
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

}
