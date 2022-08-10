package com.orion.ops.entity.request.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用操作日志请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/9 14:44
 */
@Data
@ApiModel(value = "应用操作日志请求")
public class ApplicationActionLogRequest {

    @ApiModelProperty(value = "id")
    private Long id;

}
