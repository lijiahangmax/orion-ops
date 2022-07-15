package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 应用流水线任务详情状态响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/24 16:53
 */
@Data
@ApiModel(value = "应用流水线任务详情状态响应")
public class ApplicationPipelineTaskDetailStatusVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "流水线任务id")
    private Long taskId;

    @ApiModelProperty(value = "relId")
    private Long relId;

    /**
     * @see com.orion.ops.constant.app.PipelineDetailStatus
     */
    @ApiModelProperty(value = "状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止")
    private Integer status;

    @ApiModelProperty(value = "执行开始时间")
    private Date startTime;

    @ApiModelProperty(value = "执行开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "执行结束时间")
    private Date endTime;

    @ApiModelProperty(value = "执行结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDetailDO.class, ApplicationPipelineTaskDetailStatusVO.class, p -> {
            ApplicationPipelineTaskDetailStatusVO vo = new ApplicationPipelineTaskDetailStatusVO();
            vo.setId(p.getId());
            vo.setTaskId(p.getTaskId());
            vo.setRelId(p.getRelId());
            vo.setStatus(p.getExecStatus());
            Date startTime = p.getExecStartTime();
            Date endTime = p.getExecEndTime();
            vo.setStartTime(startTime);
            vo.setEndTime(endTime);
            Optional.ofNullable(startTime).map(Dates::ago).ifPresent(vo::setStartTimeAgo);
            Optional.ofNullable(endTime).map(Dates::ago).ifPresent(vo::setEndTimeAgo);
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

}
