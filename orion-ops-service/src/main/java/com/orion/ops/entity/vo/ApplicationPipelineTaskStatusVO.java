package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationPipelineTaskDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 应用流水线任务状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/24 16:53
 */
@Data
public class ApplicationPipelineTaskStatusVO {

    /**
     * id
     */
    private Long id;

    /**
     * 状态
     *
     * @see com.orion.ops.consts.app.PipelineStatus
     */
    private Integer status;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 开始时间
     */
    private String startTimeAgo;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 结束时间
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
     * detail
     */
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
