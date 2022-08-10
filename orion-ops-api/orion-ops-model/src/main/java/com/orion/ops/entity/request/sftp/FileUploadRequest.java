package com.orion.ops.entity.request.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * sftp 上传文件请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/28 18:31
 */
@Data
@ApiModel(value = "上传文件请求")
public class FileUploadRequest {

    @ApiModelProperty(value = "sessionToken")
    private String sessionToken;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "文件token")
    private String fileToken;

    @ApiModelProperty(value = "本地文件路径")
    private String localPath;

    @ApiModelProperty(value = "远程文件路径")
    private String remotePath;

    @ApiModelProperty(value = "文件大小")
    private Long size;

}
