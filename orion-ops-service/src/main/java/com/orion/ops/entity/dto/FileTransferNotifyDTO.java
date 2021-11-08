package com.orion.ops.entity.dto;

import lombok.Data;

/**
 * sftp 通知
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/27 1:02
 */
@Data
public class FileTransferNotifyDTO {

    /**
     * 通知类型
     *
     * @see com.orion.ops.consts.sftp.SftpNotifyType
     */
    private Integer type;

    /**
     * fileToken
     */
    private String fileToken;

    /**
     * body
     */
    private Object body;

    public static FileTransferNotifyProgress progress(String rate, String current, String progress) {
        FileTransferNotifyProgress notifyProgress = new FileTransferNotifyProgress();
        notifyProgress.setRate(rate);
        notifyProgress.setCurrent(current);
        notifyProgress.setProgress(progress);
        return notifyProgress;
    }

    @Data
    public static class FileTransferNotifyProgress {

        /**
         * 速度
         */
        private String rate;

        /**
         * 当前位置
         */
        private String current;

        /**
         * 进度
         */
        private String progress;

    }

}
