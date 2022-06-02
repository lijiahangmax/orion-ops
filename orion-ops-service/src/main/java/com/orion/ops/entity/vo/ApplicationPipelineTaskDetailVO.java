package com.orion.ops.entity.vo;

import com.alibaba.fastjson.JSON;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import com.orion.ops.entity.dto.ApplicationPipelineStageConfigDTO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.Converts;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 流水线明细详情
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/8 10:37
 */
@Data
public class ApplicationPipelineTaskDetailVO {

    /**
     * id
     */
    private Long id;

    /**
     * 流水线任务id
     */
    private Long taskId;

    /**
     * 引用id
     */
    private Long relId;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用唯一标识
     */
    private String appTag;

    /**
     * 阶段类型 10构建 20发布
     *
     * @see com.orion.ops.consts.app.StageType
     */
    private Integer stageType;

    /**
     * 阶段操作配置
     */
    private ApplicationPipelineStageConfigVO config;

    /**
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止
     *
     * @see com.orion.ops.consts.app.PipelineDetailStatus
     */
    private Integer status;

    /**
     * 执行开始时间
     */
    private Date startTime;

    /**
     * 执行开始时间
     */
    private String startTimeAgo;

    /**
     * 执行结束时间
     */
    private Date endTime;

    /**
     * 执行结束时间
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

    static {
        TypeStore.STORE.register(ApplicationPipelineTaskDetailDO.class, ApplicationPipelineTaskDetailVO.class, p -> {
            ApplicationPipelineTaskDetailVO vo = new ApplicationPipelineTaskDetailVO();
            vo.setId(p.getId());
            vo.setTaskId(p.getTaskId());
            vo.setAppId(p.getAppId());
            vo.setRelId(p.getRelId());
            vo.setAppName(p.getAppName());
            vo.setAppTag(p.getAppTag());
            vo.setStageType(p.getStageType());
            ApplicationPipelineStageConfigDTO config = JSON.parseObject(p.getStageConfig(), ApplicationPipelineStageConfigDTO.class);
            vo.setConfig(Converts.to(config, ApplicationPipelineStageConfigVO.class));
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
