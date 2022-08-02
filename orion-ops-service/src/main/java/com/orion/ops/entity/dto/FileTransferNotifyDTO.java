package com.orion.ops.entity.dto;

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

    public static FileTransferNotifyProgress progress(String rate, String current, String progress) {
        FileTransferNotifyProgress notifyProgress = new FileTransferNotifyProgress();
        notifyProgress.setRate(rate);
        notifyProgress.setCurrent(current);
        notifyProgress.setProgress(progress);
        return notifyProgress;
    }

    @Data
    @ApiModel(value = "文件传输进度")
    // TODO
    public static class FileTransferNotifyProgress {

        @ApiModelProperty(value = "速度")
        private String rate;

        @ApiModelProperty(value = "当前位置")
        private String current;

        @ApiModelProperty(value = "进度")
        private String progress;

    }

}
