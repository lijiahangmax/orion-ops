package com.orion.ops.entity.vo.sftp;

import lombok.Data;

/**
 * 命令提交结果vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/18 22:25
 */
@Data
public class CommandTaskSubmitVO {

    /**
     * 执行id
     */
    private Long execId;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器名称
     */
    private String machineName;

    /**
     * 机器主机
     */
    private String machineHost;

}
