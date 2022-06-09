package com.orion.ops.entity.vo;

import com.alibaba.fastjson.JSON;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import com.orion.ops.entity.dto.ApplicationPipelineStageConfigDTO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.Converts;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 流水线明细详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/8 10:37
 */
@Data
@ApiModel(value = "流水线明细详情响应")
public class ApplicationPipelineTaskDetailVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "流水线任务id")
    private Long taskId;

    @ApiModelProperty(value = "引用id")
    private Long relId;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用唯一标识")
    private String appTag;

    /**
     * @see com.orion.ops.consts.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    private Integer stageType;

    @ApiModelProperty(value = "阶段操作配置")
    private ApplicationPipelineStageConfigVO config;

    /**
     * @see com.orion.ops.consts.app.PipelineDetailStatus
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
