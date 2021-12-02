package com.orion.ops.entity.request;

import lombok.Data;

import java.util.List;

/**
 * app配置请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 18:50
 */
@Data
public class ApplicationConfigRequest {

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 阶段类型
     *
     * @see com.orion.ops.consts.app.StageType
     */
    private Integer stageType;

    /**
     * 应用环境变量
     */
    private ApplicationConfigEnvRequest env;

    /**
     * 机器id
     */
    private List<Long> machineIdList;

    /**
     * 构建操作
     */
    private List<ApplicationConfigActionRequest> buildActions;

    /**
     * 发布操作
     */
    private List<ApplicationConfigActionRequest> releaseActions;

}
