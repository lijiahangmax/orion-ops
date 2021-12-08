package com.orion.ops.entity.vo;

import lombok.Data;

import java.util.Map;

/**
 * 应用构建状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/3 14:11
 */
@Data
public class ApplicationBuildStatusVO {

    /**
     * @see com.orion.ops.consts.app.BuildStatus
     */
    private Integer status;

    /**
     * key: action.id
     * value: status
     *
     * @see com.orion.ops.consts.app.ActionStatus
     */
    private Map<String, Integer> actionStatus;

}
