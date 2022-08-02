package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用流水线详情
 *
 * @author Jiahang Li
 * @since 2022-04-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "应用流水线详情")
@TableName("application_pipeline_detail")
public class ApplicationPipelineDetailDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "流水线id")
    @TableField("pipeline_id")
    private Long pipelineId;

    @ApiModelProperty(value = "应用id")
    @TableField("app_id")
    private Long appId;

    @ApiModelProperty(value = "环境id")
    @TableField("profile_id")
    private Long profileId;

    /**
     * @see com.orion.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布")
    @TableField("stage_type")
    private Integer stageType;

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
