package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用流水线任务日志
 *
 * @author Jiahang Li
 * @since 2022-04-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "应用流水线任务日志")
@TableName("application_pipeline_task_log")
public class ApplicationPipelineTaskLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "流水线任务id")
    @TableField("task_id")
    private Long taskId;

    @ApiModelProperty(value = "流水线任务详情id")
    @TableField("task_detail_id")
    private Long taskDetailId;

    /**
     * @see com.orion.ops.constant.app.PipelineLogStatus
     */
    @ApiModelProperty(value = "日志状态 10创建 20执行 30成功 40失败 50停止 60跳过")
    @TableField("log_status")
    private Integer logStatus;

    /**
     * @see com.orion.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    @TableField("stage_type")
    private Integer stageType;

    @ApiModelProperty(value = "日志详情")
    @TableField("log_info")
    private String logInfo;

    /**
     * @see com.orion.ops.constant.Const#NOT_DELETED
     * @see com.orion.ops.constant.Const#IS_DELETED
     */
    @ApiModelProperty(value = "是否删除 1未删除 2已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("update_time")
    private Date updateTime;

}
