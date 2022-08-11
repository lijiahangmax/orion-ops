package com.orion.ops.entity.request.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sftp 截断文件请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/24 19:10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "截断文件请求")
public class FileTruncateRequest extends FileBaseRequest {

    @ApiModelProperty(value = "绝对路径")
    private String path;

}
