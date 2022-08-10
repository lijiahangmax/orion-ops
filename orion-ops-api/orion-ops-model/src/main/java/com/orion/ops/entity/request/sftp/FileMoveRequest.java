package com.orion.ops.entity.request.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sftp 移动文件请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 20:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "移动文件请求")
public class FileMoveRequest extends FileBaseRequest {

    @ApiModelProperty(value = "绝对路径")
    private String source;

    @ApiModelProperty(value = "路径 绝对路径/相对路径 可以包含../")
    private String target;

}
