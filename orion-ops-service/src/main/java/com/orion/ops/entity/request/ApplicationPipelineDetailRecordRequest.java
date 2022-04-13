package com.orion.ops.entity.request;

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
public class ApplicationPipelineDetailRecordRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 分支名称
     */
    private String branchName;

    /**
     * 提交id
     */
    private String commitId;

    /**
     * 构建id
     */
    private Long buildId;

    /**
     * 发布标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 发布机器id
     */
    private List<Long> machineIdList;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 阶段类型 10构建 20发布
     */
    private Integer stageType;

}
