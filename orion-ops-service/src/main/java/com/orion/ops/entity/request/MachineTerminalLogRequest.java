package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 终端日志
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 20:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MachineTerminalLogRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

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
    private Integer machineId;

    /**
     * 机器名称
     */
    private String machineName;

    /**
     * 机器host
     */
    private String machineHost;

    /**
     * token
     */
    private String accessToken;

    /**
     * 建立连接时间 开始
     */
    private Date connectedTimeStart;

    /**
     * 建立连接时间 结束
     */
    private Date connectedTimeEnd;

    /**
     * 断开连接时间 开始
     */
    private Date disconnectedTimeStart;

    /**
     * 断开连接时间 结束
     */
    private Date disconnectedTimeEnd;

    /**
     * close code
     */
    private Integer closeCode;

}
