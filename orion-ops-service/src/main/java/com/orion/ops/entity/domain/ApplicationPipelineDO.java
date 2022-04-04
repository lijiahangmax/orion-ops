package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用流水线
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-04-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application_pipeline")
public class ApplicationPipelineDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 环境id
     */
    @TableField("profile_id")
    private Long profileId;

    /**
     * 流水线名称
     */
    @TableField("pipeline_name")
    private String pipelineName;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 是否删除 1未删除 2已删除
     *
     * @see com.orion.ops.consts.Const#NOT_DELETED
     * @see com.orion.ops.consts.Const#IS_DELETED
     */
    @TableLogic
    private Integer deleted;

    /**
     * 修改时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 创建时间
     */
    @TableField("update_time")
    private Date updateTime;

}
