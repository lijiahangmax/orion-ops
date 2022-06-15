package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应用配置请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 18:50
 */
@Data
@ApiModel(value = "应用配置请求")
public class ApplicationConfigRequest {

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    /**
     * @see com.orion.ops.consts.app.StageType
     */
    @ApiModelProperty(value = "阶段类型")
    private Integer stageType;

    @ApiModelProperty(value = "应用环境变量")
    private ApplicationConfigEnvRequest env;

    @ApiModelProperty(value = "机器id")
    private List<Long> machineIdList;

    @ApiModelProperty(value = "构建操作")
    private List<ApplicationConfigActionRequest> buildActions;

    @ApiModelProperty(value = "发布操作")
    private List<ApplicationConfigActionRequest> releaseActions;

}
