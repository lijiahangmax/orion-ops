package com.orion.ops.entity.dto;

import lombok.Data;

/**
 * 检查应用是否已配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/29 23:41
 */
@Data
public class ApplicationActionConfigDTO {

    /**
     * appId
     */
    private Long appId;

    /**
     * 构建阶段数量
     */
    private Integer buildStageCount;

    /**
     * 发布阶段数量
     */
    private Integer releaseStageCount;

}
