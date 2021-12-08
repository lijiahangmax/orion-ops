package com.orion.ops.entity.vo;

import lombok.Data;

import java.util.List;

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
     * 状态
     *
     * @see com.orion.ops.consts.app.BuildStatus
     */
    private Integer status;

    /**
     * action
     */
    private List<ApplicationBuildActionStatusVO> actions;

}
