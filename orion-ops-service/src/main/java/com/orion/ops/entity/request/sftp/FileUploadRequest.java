package com.orion.ops.entity.request.sftp;

import lombok.Data;

/**
 * 上传文件请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/28 18:31
 */
@Data
public class FileUploadRequest {

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 文件token
     */
    private String fileToken;

    /**
     * 本地文件路径
     */
    private String localPath;

    /**
     * 远程文件路径
     */
    private String remotePath;

    /**
     * 文件大小
     */
    private Long size;

}
