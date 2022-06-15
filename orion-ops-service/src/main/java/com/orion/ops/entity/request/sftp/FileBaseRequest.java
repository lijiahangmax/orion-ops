package com.orion.ops.entity.request.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * sftp 请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 19:28
 */
@Data
@ApiModel(value = "sftp请求")
public class FileBaseRequest {

    @ApiModelProperty(value = "sessionToken")
    private String sessionToken;

    @ApiModelProperty(value = "是否查询隐藏文件")
    private Boolean all;

}
