package com.orion.ops.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * sftp 会话信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/16 9:41
 */
@Data
public class SftpSessionTokenDTO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器id (批量上传用)
     */
    private List<Long> machineIdList;

}
