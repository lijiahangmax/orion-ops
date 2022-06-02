package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 终端管理
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 23:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MachineTerminalManagerRequest extends PageRequest {

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
     * 连接时间 开始
     */
    private Date connectedTimeStart;

    /**
     * 连接时间 结束
     */
    private Date connectedTimeEnd;

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

    /**
     * 机器唯一标识
     */
    private String machineTag;

}
