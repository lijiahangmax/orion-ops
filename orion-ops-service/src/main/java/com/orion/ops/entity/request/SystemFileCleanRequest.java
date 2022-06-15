package com.orion.ops.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件清理请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/17 23:43
 */
@Data
@ApiModel(value = "文件清理请求")
public class SystemFileCleanRequest {

    /**
     * @see com.orion.ops.consts.system.SystemCleanType
     */
    @ApiModelProperty(value = "文件清理类型")
    private Integer cleanType;

}
