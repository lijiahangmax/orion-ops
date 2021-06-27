package com.orion.ops.handler.sftp;

import com.orion.ops.consts.sftp.SftpTransferType;
import lombok.Data;

/**
 * 文件传输hint
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/26 12:35
 */
@Data
public class FileTransferHint {

    /**
     * 恢复传输id
     */
    private Long resumeId;

    /**
     * token
     */
    private String token;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 本地文件
     */
    private String localFile;

    /**
     * 远程文件
     */
    private String remoteFile;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 编码格式
     */
    private String charset;

    /**
     * 传输类型
     *
     * @see SftpTransferType
     */
    private SftpTransferType transferType;

}
