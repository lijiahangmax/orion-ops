package com.orion.ops.entity.vo;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

/**
 * 系统版本响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/9 13:53
 */
@Data
@Builder
@ApiModel(value = "系统配置响应")
public class SystemVersionVO {

    /**
     * orion-kit 版本
     */
    private String orionKitVersion;

    /**
     * orion-ops 版本
     */
    private String orionOpsVersion;

}
