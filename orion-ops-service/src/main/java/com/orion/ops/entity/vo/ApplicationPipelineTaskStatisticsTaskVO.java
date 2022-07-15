package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.constant.app.PipelineStatus;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 应用流水线统计分析操作日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/10 15:01
 */
@Data
@ApiModel(value = "应用流水线统计分析操作日志响应")
public class ApplicationPipelineTaskStatisticsTaskVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "执行时间")
    private Date execDate;

    /**
     * @see PipelineStatus
     */
    @ApiModelProperty(value = "执行状态 10待审核 20审核驳回 30待执行 35待调度 40执行中 50执行完成 60执行停止 70执行失败")
    private Integer status;

    @ApiModelProperty(value = "成功执行操作时长毫秒")
    private Long used;

    @ApiModelProperty(value = "成功执行操作时长")
    private String usedInterval;

    @ApiModelProperty(value = "执行操作")
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
