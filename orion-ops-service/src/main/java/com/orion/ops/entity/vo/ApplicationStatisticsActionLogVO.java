package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationActionLogDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;

/**
 * 应用构建统计分析操作日志
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 18:07
 */
@Data
public class ApplicationStatisticsActionLogVO {

    /**
     * id
     */
    private Long id;

    /**
     * 操作id
     */
    private Long actionId;

    /**
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已取消
     *
     * @see com.orion.ops.consts.app.ActionStatus
     */
    private Integer status;

    /**
     * 操作时长 (任何状态)
     */
    private Long used;

    /**
     * 操作时长 (任何状态)
     */
    private String usedInterval;

    static {
        TypeStore.STORE.register(ApplicationActionLogDO.class, ApplicationStatisticsActionLogVO.class, p -> {
            ApplicationStatisticsActionLogVO vo = new ApplicationStatisticsActionLogVO();
            vo.setId(p.getId());
            vo.setStatus(p.getRunStatus());
            Date startTime = p.getStartTime();
            Date endTime = p.getEndTime();
            if (startTime != null && endTime != null) {
                long used = endTime.getTime() - startTime.getTime();
                vo.setUsed(used);
                vo.setUsedInterval(Utils.interval(used));
            }
            return vo;
        });
    }

}
