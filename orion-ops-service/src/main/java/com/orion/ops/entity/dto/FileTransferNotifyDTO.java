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
     * body
     */
    private Object body;

    /**
     * fileToken
     */
    private String fileToken;

    @Data
    public static class FileTransferNotifyProgress {

        /**
         * 速度
         */
        private String rate;

        /**
         * 进度
         */
        private String progress;

        /**
         * 当前
         */
        private String current;

    }

}
