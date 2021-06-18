package com.orion.ops.entity.dto;

import lombok.Data;

/**
 * 文件tail 对象
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/10 18:53
 */
@Data
public class FileTailDTO {

    /**
     * 文件绝对路径
     */
    private String filePath;

    /**
     * 下载用户id
     */
    private Long userId;

    /**
     * 机器id
     */
    private Long machineId;

}
