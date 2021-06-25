package com.orion.ops.entity.request.sftp;

import com.orion.remote.channel.sftp.SftpExecutor;
import lombok.Data;

/**
 * sftp 请求request
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 19:28
 */
@Data
public class FileBaseRequest {

    /**
     * token
     */
    private String token;

    /**
     * 当前路径
     */
    private String current;

    /**
     * 是否查询隐藏文件
     * <p>
     * 0 不查询
     * 1 查询
     */
    private int all;

    /**
     * executor
     */
    private SftpExecutor executor;

    public FileBaseRequest() {
        this.all = 0;
    }

}
