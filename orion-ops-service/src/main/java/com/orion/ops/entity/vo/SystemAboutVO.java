package com.orion.ops.entity.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统版本响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/9 13:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "系统信息响应")
public class SystemAboutVO {

    /**
     * orion-kit 版本
     */
    private String orionKitVersion;

    /**
     * orion-ops 版本
     */
    private String orionOpsVersion;

    /**
     * 作者
     */
    private String author;

    /**
     * 作者
     */
    private String authorCn;

}
