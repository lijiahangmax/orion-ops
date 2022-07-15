package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 流水线详情明细请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 10:46
 */
@Data
@ApiModel(value = "流水线详情明细请求")
public class ApplicationPipelineTaskDetailRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "分支名称")
    private String branchName;

    @ApiModelProperty(value = "提交id")
    private String commitId;

    @ApiModelProperty(value = "构建id")
    private Long buildId;

    @ApiModelProperty(value = "发布标题")
    private String title;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "发布机器id")
    private List<Long> machineIdList;

    @ApiModelProperty(value = "应用id", hidden = true)
    private Long appId;

    @ApiModelProperty(value = "环境id", hidden = true)
    private Long profileId;

    /**
     * @see com.orion.ops.constant.app.StageType
     */
    @ApiModelProperty(value = "阶段类型 10构建 20发布", hidden = true)
    private Integer stageType;

}
