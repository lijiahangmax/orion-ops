package com.orion.ops.entity.request.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * sftp 打开请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 19:50
 */
@Data
@ApiModel(value = "sftp打开请求")
public class FileOpenRequest {

    @ApiModelProperty(value = "机器id")
    private Long machineId;

}
