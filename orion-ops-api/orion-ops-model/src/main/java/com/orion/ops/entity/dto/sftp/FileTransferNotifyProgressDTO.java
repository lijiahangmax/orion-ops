package com.orion.ops.entity.dto.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 文件传输进度
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/2 11:32
 */
@Data
@AllArgsConstructor
@ApiModel(value = "文件传输进度")
public class FileTransferNotifyProgressDTO {

    @ApiModelProperty(value = "速度")
    private String rate;

    @ApiModelProperty(value = "当前位置")
    private String current;

    @ApiModelProperty(value = "进度")
    private String progress;

}
