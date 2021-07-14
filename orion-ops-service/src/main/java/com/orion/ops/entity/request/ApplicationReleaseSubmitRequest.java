package com.orion.ops.entity.request;

import lombok.Data;

import java.util.List;

/**
 * 提交上线单
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/10 20:37
 */
@Data
public class ApplicationReleaseSubmitRequest {

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 版本控制url
     */
    private String vcsUrl;

    /**
     * 分支
     */
    private String branchName;

    /**
     * 提交id
     */
    private String commitId;

    /**
     * 提交message
     */
    private String commitMessage;

    /**
     * app机器id列表
     *
     * @see com.orion.ops.entity.domain.ApplicationMachineDO#setId(Long)
     */
    private List<Long> appMachineIdList;

}
