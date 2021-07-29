package com.orion.ops.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 应用分支提交信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/28 19:29
 */
@Data
public class ApplicationVcsInfoVO {

    /**
     * 分支
     */
    private List<ApplicationVcsBranchVO> branches;

    /**
     * 日志
     */
    private List<ApplicationVcsCommitVO> commits;

}
