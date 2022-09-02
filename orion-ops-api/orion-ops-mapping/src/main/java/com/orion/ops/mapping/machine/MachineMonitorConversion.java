package com.orion.ops.mapping.machine;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.constant.monitor.MonitorConst;
import com.orion.ops.entity.domain.MachineMonitorDO;
import com.orion.ops.entity.dto.MachineMonitorDTO;
import com.orion.ops.entity.vo.machine.MachineMonitorVO;

/**
 * 机器监控 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:11
 */
public class MachineMonitorConversion {

    static {
        TypeStore.STORE.register(MachineMonitorDO.class, MachineMonitorVO.class, p -> {
            MachineMonitorVO vo = new MachineMonitorVO();
            vo.setId(p.getId());
            vo.setMachineId(p.getMachineId());
            vo.setStatus(p.getMonitorStatus());
            vo.setUrl(p.getMonitorUrl());
            vo.setAccessToken(p.getAccessToken());
            vo.setCurrentVersion(p.getAgentVersion());
            vo.setLatestVersion(MonitorConst.LATEST_VERSION);
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(MachineMonitorDTO.class, MachineMonitorVO.class, p -> {
            MachineMonitorVO vo = new MachineMonitorVO();
            vo.setId(p.getId());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineHost(p.getMachineHost());
            vo.setStatus(p.getMonitorStatus());
            vo.setUrl(p.getMonitorUrl());
            vo.setAccessToken(p.getAccessToken());
            vo.setCurrentVersion(p.getAgentVersion());
            vo.setLatestVersion(MonitorConst.LATEST_VERSION);
            return vo;
        });
    }

}
