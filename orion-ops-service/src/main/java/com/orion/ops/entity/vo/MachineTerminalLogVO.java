package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 终端日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/19 20:59
 */
@Data
@ApiModel(value = "终端日志响应")
public class MachineTerminalLogVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器唯一标识")
    private String machineTag;

    @ApiModelProperty(value = "机器host")
    private String machineHost;

    @ApiModelProperty(value = "访问token")
    private String accessToken;

    @ApiModelProperty(value = "建立连接时间")
    private Date connectedTime;

    @ApiModelProperty(value = "断开连接时间")
    private Date disconnectedTime;

    @ApiModelProperty(value = "建立连接时间")
    private String connectedTimeAgo;

    @ApiModelProperty(value = "断开连接时间")
    private String disconnectedTimeAgo;

    /**
     * @see com.orion.ops.consts.ws.WsCloseCode
     */
    @ApiModelProperty(value = "close code")
    private Integer closeCode;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    static {
        TypeStore.STORE.register(MachineTerminalLogDO.class, MachineTerminalLogVO.class, p -> {
            MachineTerminalLogVO vo = new MachineTerminalLogVO();
            vo.setId(p.getId());
            vo.setUserId(p.getUserId());
            vo.setUsername(p.getUsername());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineTag(p.getMachineTag());
            vo.setMachineHost(p.getMachineHost());
            vo.setAccessToken(p.getAccessToken());
            vo.setConnectedTime(p.getConnectedTime());
            vo.setDisconnectedTime(p.getDisconnectedTime());
            Optional.ofNullable(p.getConnectedTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setConnectedTimeAgo);
            Optional.ofNullable(p.getDisconnectedTime())
                    .map(Dates::ago)
                    .ifPresent(vo::setDisconnectedTimeAgo);
            vo.setCloseCode(p.getCloseCode());
            vo.setCreateTime(p.getCreateTime());
            return vo;
        });
    }

}
