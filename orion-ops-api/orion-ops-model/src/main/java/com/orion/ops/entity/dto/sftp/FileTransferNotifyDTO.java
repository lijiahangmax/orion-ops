package com.orion.ops.entity.dto.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * sftp 文件传输通知
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/27 1:02
 */
@Data
@ApiModel(value = "sftp 文件传输通知")
@SuppressWarnings("ALL")
public class FileTransferNotifyDTO {

    /**
     * @see com.orion.ops.constant.sftp.SftpNotifyType
     */
    @ApiModelProperty(value = "通知类型")
    private Integer type;

    @ApiModelProperty(value = "fileToken")
    private String fileToken;

    @ApiModelProperty(value = "body")
    private Object body;

}
