package com.orion.ops.entity.vo.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 分支提交信息响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 17:57
 */
@Data
@ApiModel(value = "分支提交信息响应")
public class ApplicationRepositoryCommitVO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "提交信息")
    private String message;

    @ApiModelProperty(value = "提交人")
    private String name;

    @ApiModelProperty(value = "提交时间")
    private Date time;

    @ApiModelProperty(value = "提交时间")
    private String timeAgo;

}
