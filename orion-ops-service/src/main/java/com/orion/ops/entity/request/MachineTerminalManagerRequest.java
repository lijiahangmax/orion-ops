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
     * 连接主机
     */
    private String host;

}
