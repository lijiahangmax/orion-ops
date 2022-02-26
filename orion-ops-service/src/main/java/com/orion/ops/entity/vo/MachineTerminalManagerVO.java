package com.orion.ops.entity.vo;

import com.orion.ops.handler.terminal.TerminalConnectHint;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 机器终端 管理员面板
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 22:42
 */
@Data
public class MachineTerminalManagerVO {

    /**
     * token
     */
    private String token;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * username
     */
    private String userName;

    /**
     * 连接时间
     */
    private Date connectedTime;

    /**
     * 连接时间
     */
    private String connectedTimeAgo;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器名称
     */
    private String machineName;

    /**
     * 机器host
     */
    private String machineHost;

    /**
     * 机器tag
     */
    private String machineTag;

    /**
     * logId
     */
    private Long logId;

    static {
        TypeStore.STORE.register(TerminalConnectHint.class, MachineTerminalManagerVO.class, p -> {
            MachineTerminalManagerVO vo = new MachineTerminalManagerVO();
            vo.setUserId(p.getUserId());
            vo.setUserName(p.getUsername());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineHost(p.getMachineHost());
            vo.setMachineTag(p.getMachineTag());
            vo.setLogId(p.getLogId());
            vo.setConnectedTime(p.getConnectedTime());
            Optional.ofNullable(p.getConnectedTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setConnectedTimeAgo);
            return vo;
        });
    }

}
