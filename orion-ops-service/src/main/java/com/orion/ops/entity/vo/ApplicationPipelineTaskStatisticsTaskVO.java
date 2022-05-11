package com.orion.ops.entity.vo;

import com.orion.ops.consts.app.PipelineStatus;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 应用流水线统计分析操作日志
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 15:01
 */
@Data
public class ApplicationPipelineTaskStatisticsTaskVO {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 执行时间
     */
    private Date execDate;

    /**
     * 执行状态 10待审核 20审核驳回 30待执行 35待调度 40执行中 50执行完成 60执行停止 70执行失败
     *
     * @see PipelineStatus
     */
    private Integer status;

    /**
     * 执行操作时长ms (成功)
     */
    private Long used;

    /**
     * 执行操作时长 (成功)
     */
    private String usedInterval;

    /**
     * 执行操作
     */
    private List<ApplicationPipelineTaskStatisticsDetailVO> details;

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDO.class, ApplicationPipelineTaskStatisticsTaskVO.class, p -> {
            ApplicationPipelineTaskStatisticsTaskVO vo = new ApplicationPipelineTaskStatisticsTaskVO();
            vo.setId(p.getId());
            vo.setTitle(p.getExecTitle());
            vo.setExecDate(p.getExecStartTime());
            vo.setStatus(p.getExecStatus());
            // 设置构建用时
            if (PipelineStatus.FINISH.getStatus().equals(p.getExecStatus())
                    && p.getExecStartTime() != null
                    && p.getExecEndTime() != null) {
                long used = p.getExecEndTime().getTime() - p.getExecStartTime().getTime();
                vo.setUsed(used);
                vo.setUsedInterval(Utils.interval(used));
            }
            return vo;
        });
    }

}
