package com.orion.ops.entity.request.sftp;

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
public class FileDownloadRequest extends FileBaseRequest {

    /**
     * 路径 绝对路径
     */
    private List<String> paths;

}
