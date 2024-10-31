/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.mapping.machine;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.ops.constant.monitor.MonitorConst;
import cn.orionsec.ops.entity.domain.MachineMonitorDO;
import cn.orionsec.ops.entity.dto.MachineMonitorDTO;
import cn.orionsec.ops.entity.vo.machine.MachineMonitorVO;

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
