package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationPipelineTaskLogDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;

/**
 * 流水线日志
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/24 20:49
 */
@Data
public class ApplicationPipelineTaskLogVO {

    /**
     * id
     */
    private Long id;

    /**
     * 流水线任务id
     */
    private Long taskId;

    /**
     * 流水线任务详情id
     */
    private Long taskDetailId;

    /**
     * 日志状态 10创建 20执行 30成功 40失败 50停止 60跳过
     *
     * @see com.orion.ops.consts.app.PipelineLogStatus
     */
    private Integer status;

    /**
     * 阶段类型 10构建 20发布
     *
     * @see com.orion.ops.consts.app.StageType
     */
    private Integer type;

    /**
     * 日志详情
     */
    private String log;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间
     */
    private String createTimeAgo;

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskLogDO.class, ApplicationPipelineTaskLogVO.class, p -> {
            ApplicationPipelineTaskLogVO vo = new ApplicationPipelineTaskLogVO();
            vo.setId(p.getId());
            vo.setTaskId(p.getTaskId());
            vo.setTaskDetailId(p.getTaskDetailId());
            vo.setStatus(p.getLogStatus());
            vo.setType(p.getStageType());
            vo.setLog(p.getLogInfo());
            vo.setCreateTime(p.getCreateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            return vo;
        });
    }

}
