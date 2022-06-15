package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationPipelineTaskLogDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 流水线日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/24 20:49
 */
@Data
@ApiModel(value = "流水线日志响应")
public class ApplicationPipelineTaskLogVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "流水线任务id")
    private Long taskId;

    @ApiModelProperty(value = "流水线任务详情id")
    private Long taskDetailId;

    /**
     * @see com.orion.ops.consts.app.PipelineLogStatus
     */
    @ApiModelProperty(value = "日志状态 10创建 20执行 30成功 40失败 50停止 60跳过")
    private Integer status;

    /**
     * @see com.orion.ops.consts.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    private Integer type;

    @ApiModelProperty(value = "日志详情")
    private String log;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
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
