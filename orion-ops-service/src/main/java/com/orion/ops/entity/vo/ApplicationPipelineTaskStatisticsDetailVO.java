package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 应用流水线统计分析操作日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 15:05
 */
@Data
@ApiModel(value = "应用流水线统计分析操作日志响应")
public class ApplicationPipelineTaskStatisticsDetailVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "操作id")
    private Long detailId;

    @ApiModelProperty(value = "引用id")
    private Long relId;

    /**
     * @see com.orion.ops.consts.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    private Integer stageType;

    /**
     * @see com.orion.ops.consts.app.PipelineDetailStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止")
    private Integer status;

    @ApiModelProperty(value = "操作时长 (任何状态)")
    private Long used;

    @ApiModelProperty(value = "操作时长 (任何状态)")
    private String usedInterval;

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDetailDO.class, ApplicationPipelineTaskStatisticsDetailVO.class, p -> {
            ApplicationPipelineTaskStatisticsDetailVO vo = new ApplicationPipelineTaskStatisticsDetailVO();
            vo.setId(p.getId());
            vo.setRelId(p.getRelId());
            vo.setStageType(p.getStageType());
            vo.setStatus(p.getExecStatus());
            Date startTime = p.getExecStartTime();
            Date endTime = p.getExecEndTime();
            if (startTime != null && endTime != null) {
                long used = endTime.getTime() - startTime.getTime();
                vo.setUsed(used);
                vo.setUsedInterval(Utils.interval(used));
            }
            return vo;
        });
    }

}
