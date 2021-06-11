package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 终端日志
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 20:59
 */
@Data
public class MachineTerminalLogVO {

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
    private Long machineId;

    /**
     * 机器host
     */
    private String machineHost;

    /**
     * token
     */
    private String accessToken;

    /**
     * 建立连接时间
     */
    private Date connectedTime;

    /**
     * 断开连接时间
     */
    private Date disconnectedTime;

    /**
     * 建立连接时间
     */
    private String connectedTimeAgo;

    /**
     * 断开连接时间
     */
    private String disconnectedTimeAgo;

    /**
     * close code
     */
    private Integer closeCode;

    /**
     * 创建时间
     */
    private Date createTime;

    static {
        TypeStore.STORE.register(MachineTerminalLogDO.class, MachineTerminalLogVO.class, p -> {
            MachineTerminalLogVO vo = new MachineTerminalLogVO();
            vo.setId(p.getId());
            vo.setUserId(p.getUserId());
            vo.setUsername(p.getUsername());
            vo.setMachineId(p.getMachineId());
            vo.setMachineHost(p.getMachineHost());
            vo.setAccessToken(p.getAccessToken());
            vo.setConnectedTime(p.getConnectedTime());
            vo.setDisconnectedTime(p.getDisconnectedTime());
            Optional.ofNullable(p.getConnectedTime()).map(Dates::ago).ifPresent(vo::setConnectedTimeAgo);
            Optional.ofNullable(p.getDisconnectedTime()).map(Dates::ago).ifPresent(vo::setDisconnectedTimeAgo);
            vo.setCloseCode(p.getCloseCode());
            vo.setCreateTime(p.getCreateTime());
            return vo;
        });
    }

}
