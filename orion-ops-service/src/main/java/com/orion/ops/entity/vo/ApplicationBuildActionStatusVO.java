package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationBuildActionDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 应用执行操作状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/8 15:56
 */
@Data
public class ApplicationBuildActionStatusVO {

    /**
     * id
     */
    private Long id;

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
        TypeStore.STORE.register(ApplicationBuildActionDO.class, ApplicationBuildActionStatusVO.class, p -> {
            ApplicationBuildActionStatusVO vo = new ApplicationBuildActionStatusVO();
            vo.setId(p.getId());
            vo.setStatus(p.getRunStatus());
            Date startTime = p.getStartTime(), endTime = p.getEndTime();
            vo.setStartTime(startTime);
            vo.setStartTimeAgo(Optional.ofNullable(startTime).map(Dates::ago).orElse(null));
            vo.setEndTime(endTime);
            vo.setEndTimeAgo(Optional.ofNullable(endTime).map(Dates::ago).orElse(null));
            vo.setExitCode(p.getExitCode());
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Dates.interval(vo.getUsed(), false, "d", "h", "m", "s"));
            }
            return vo;
        });
    }

}
