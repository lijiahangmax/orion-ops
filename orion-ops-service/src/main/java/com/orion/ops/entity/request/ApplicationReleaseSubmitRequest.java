package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 提交发布任务请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/10 20:37
 */
@Data
@ApiModel(value = "提交发布任务请求")
public class ApplicationReleaseSubmitRequest {

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "应用id")
    private Long appId;

    @ApiModelProperty(value = "环境id")
    private Long profileId;

    @ApiModelProperty(value = "应用版本仓库url")
    private String vcsUrl;

    @ApiModelProperty(value = "分支")
    private String branchName;

    @ApiModelProperty(value = "提交id")
    private String commitId;

    @ApiModelProperty(value = "提交message")
    private String commitMessage;

    @ApiModelProperty(value = "app机器id列表")
    private List<Long> machineIdList;

}
