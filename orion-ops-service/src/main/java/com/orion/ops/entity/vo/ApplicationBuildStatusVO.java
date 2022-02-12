package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 应用构建状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/3 14:11
 */
@Data
public class ApplicationBuildStatusVO {

    /**
     * id
     */
    private Long id;

    /**
     * 状态
     *
     * @see com.orion.ops.consts.app.BuildStatus
     */
    private Integer status;

    /**
     * 构建开始时间
     */
    private Date startTime;

    /**
     * 构建开始时间
     */
    private String startTimeAgo;

    /**
     * 构建结束时间
     */
    private Date endTime;

    /**
     * 构建结束时间
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
     * action
     */
    private List<ApplicationActionStatusVO> actions;

    static {
        TypeStore.STORE.register(ApplicationBuildDO.class, ApplicationBuildStatusVO.class, p -> {
            ApplicationBuildStatusVO vo = new ApplicationBuildStatusVO();
            vo.setId(p.getId());
            vo.setStatus(p.getBuildStatus());
            Date startTime = p.getBuildStartTime(), endTime = p.getBuildEndTime();
            vo.setStartTime(startTime);
            vo.setStartTimeAgo(Optional.ofNullable(startTime).map(Dates::ago).orElse(null));
            vo.setEndTime(endTime);
            vo.setEndTimeAgo(Optional.ofNullable(endTime).map(Dates::ago).orElse(null));
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Dates.interval(vo.getUsed(), false, "d", "h", "m", "s"));
            }
            return vo;
        });
    }

}
