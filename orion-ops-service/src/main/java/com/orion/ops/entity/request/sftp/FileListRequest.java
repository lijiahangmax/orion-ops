package com.orion.ops.entity.request.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * sftp 文件列表请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 19:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "文件列表请求")
public class FileListRequest extends FileBaseRequest {

    @ApiModelProperty(value = "绝对路径")
    private String path;

}
