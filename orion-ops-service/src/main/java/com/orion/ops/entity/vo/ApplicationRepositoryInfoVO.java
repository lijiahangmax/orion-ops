package com.orion.ops.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应用仓库分支提交信息响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/28 19:29
 */
@Data
@ApiModel(value = "应用仓库分支提交信息响应")
public class ApplicationRepositoryInfoVO {

    @ApiModelProperty(value = "分支")
    private List<ApplicationRepositoryBranchVO> branches;

    @ApiModelProperty(value = "提交记录")
    private List<ApplicationRepositoryCommitVO> commits;

}
