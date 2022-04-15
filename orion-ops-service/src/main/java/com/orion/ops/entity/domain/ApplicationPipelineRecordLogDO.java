package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用流水线执行日志
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-04-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application_pipeline_record_log")
public class ApplicationPipelineRecordLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流水线明细id
     */
    @TableField("record_id")
    private Long recordId;

    /**
     * 流水线详情明细id
     */
    @TableField("record_detail_id")
    private Long recordDetailId;

    /**
     * 日志状态 10创建 20执行 30成功 40失败 50停止 60跳过
     *
     * @see com.orion.ops.consts.app.PipelineLogStatus
     */
    @TableField("log_status")
    private Integer logStatus;

    /**
     * 阶段类型 10构建 20发布
     *
     * @see com.orion.ops.consts.app.StageType
     */
    @TableField("stage_type")
    private Integer stageType;

    /**
     * 日志详情
     */
    @TableField("log_info")
    private String logInfo;

    /**
     * 是否删除 1未删除 2已删除
     *
     * @see com.orion.ops.consts.Const#NOT_DELETED
     * @see com.orion.ops.consts.Const#IS_DELETED
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

}
