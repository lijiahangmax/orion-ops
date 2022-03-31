package com.orion.ops.entity.vo;

import com.orion.ops.consts.app.BuildStatus;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 应用构建统计分析操作日志
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/29 18:07
 */
@Data
public class ApplicationBuildStatisticsRecordVO {

    /**
     * 构建id
     */
    private Long buildId;

    /**
     * 构建序列
     */
    private Integer seq;

    /**
     * 构建时间
     */
    private Date buildDate;

    /**
     * 状态 10未开始 20执行中 30已完成 40执行失败 50已取消
     *
     * @see com.orion.ops.consts.app.BuildStatus
     */
    private Integer status;

    /**
     * 构建操作时长ms (成功)
     */
    private Long used;

    /**
     * 构建操作时长 (成功)
     */
    private String usedInterval;

    /**
     * 构建操作
     */
    private List<ApplicationStatisticsActionLogVO> actionLogs;

    static {
        TypeStore.STORE.register(ApplicationBuildDO.class, ApplicationBuildStatisticsRecordVO.class, p -> {
            ApplicationBuildStatisticsRecordVO vo = new ApplicationBuildStatisticsRecordVO();
            vo.setBuildId(p.getId());
            vo.setSeq(p.getBuildSeq());
            vo.setBuildDate(p.getBuildStartTime());
            vo.setStatus(p.getBuildStatus());
            // 设置构建用时
            if (BuildStatus.FINISH.getStatus().equals(p.getBuildStatus())
                    && p.getBuildStartTime() != null
                    && p.getBuildEndTime() != null) {
                long used = p.getBuildEndTime().getTime() - p.getBuildStartTime().getTime();
                vo.setUsed(used);
                vo.setUsedInterval(Utils.interval(used));
            }
            return vo;
        });
    }

}
