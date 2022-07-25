package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 应用流水线任务状态响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/24 16:53
 */
@Data
@ApiModel(value = "应用流水线任务状态响应")
public class ApplicationPipelineTaskStatusVO {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * @see com.orion.ops.constant.app.PipelineStatus
     */
    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "开始时间")
    private String startTimeAgo;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "结束时间")
    private String endTimeAgo;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    @ApiModelProperty(value = "流水线详情")
    private List<ApplicationPipelineTaskDetailStatusVO> details;

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDO.class, ApplicationPipelineTaskStatusVO.class, p -> {
            ApplicationPipelineTaskStatusVO vo = new ApplicationPipelineTaskStatusVO();
            vo.setId(p.getId());
            vo.setStatus(p.getExecStatus());
            Date startTime = p.getExecStartTime(), endTime = p.getExecEndTime();
            vo.setStartTime(startTime);
            vo.setStartTimeAgo(Optional.ofNullable(startTime).map(Dates::ago).orElse(null));
            vo.setEndTime(endTime);
            vo.setEndTimeAgo(Optional.ofNullable(endTime).map(Dates::ago).orElse(null));
            if (startTime != null && endTime != null) {
                vo.setUsed(endTime.getTime() - startTime.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

}
