package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用流水线执行明细详情
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application_pipeline_detail_record")
public class ApplicationPipelineDetailRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流水线id
     */
    @TableField("pipeline_id")
    private Long pipelineId;

    /**
     * 流水线详情id
     */
    @TableField("pipeline_detail_id")
    private Long pipelineDetailId;

    /**
     * 流水线操作明细id
     */
    @TableField("record_id")
    private Long recordId;

    /**
     * 应用id
     */
    @TableField("app_id")
    private Long appId;

    /**
     * 应用名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 应用tag
     */
    @TableField("app_tag")
    private String appTag;

    /**
     * 阶段类型 10构建 20发布
     */
    @TableField("stage_type")
    private Integer stageType;

    /**
     * 阶段操作配置
     */
    @TableField("stage_config")
    private String stageConfig;

    /**
     * 状态 10未开始 20进行中 30已完成 40执行失败 50已跳过 60已终止
     *
     * @see com.orion.ops.consts.app.PipelineDetailStatus
     */
    @TableField("exec_status")
    private Integer execStatus;

    /**
     * 执行开始时间
     */
    @TableField("exec_start_time")
    private Date execStartTime;

    /**
     * 执行结束时间
     */
    @TableField("exec_end_time")
    private Date execEndTime;

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
