package com.orion.ops.entity.request.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * sftp 下载请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/25 17:28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "下载请求")
public class FileDownloadRequest extends FileBaseRequest {

    @ApiModelProperty(value = "绝对路径")
    private List<String> paths;

}
