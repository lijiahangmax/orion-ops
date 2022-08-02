package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.dto.MachineMonitorDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机器代理响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 22:01
 */
@Data
@ApiModel(value = "机器代理响应")
public class MachineMonitorVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器主机")
    private String machineHost;

    /**
     * @see com.orion.ops.constant.monitor.InstallStatus
     */
    @ApiModelProperty(value = "插件安装状态 1未安装 2安装中 3已安装")
    private Integer installStatus;

    @ApiModelProperty("机器监控 url")
    private String monitorUrl;

    @ApiModelProperty("请求 accessToken")
    private String accessToken;

    @ApiModelProperty("插件版本")
    private String version;

    static {
        TypeStore.STORE.register(MachineMonitorDTO.class, MachineMonitorVO.class, p -> {
            MachineMonitorVO vo = new MachineMonitorVO();
            vo.setId(p.getId());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineHost(p.getMachineHost());
            vo.setInstallStatus(p.getInstallStatus());
            vo.setMonitorUrl(p.getMonitorUrl());
            vo.setAccessToken(p.getAccessToken());
            vo.setVersion(p.getAgentVersion());
            return vo;
        });
    }

}
