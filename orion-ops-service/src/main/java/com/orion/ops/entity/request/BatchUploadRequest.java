package com.orion.ops.entity.request;

import lombok.Data;

import java.util.List;

/**
 * 批量上传请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 16:42
 */
@Data
public class BatchUploadRequest {

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 远程路径
     */
    private String remotePath;

    /**
     * 机器id
     */
    private List<Long> machineIds;

    /**
     * 文件名称
     */
    private List<String> names;

}
