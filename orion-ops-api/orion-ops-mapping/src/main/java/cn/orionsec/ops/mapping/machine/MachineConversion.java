/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.request.machine.MachineInfoRequest;
import cn.orionsec.ops.entity.vo.machine.MachineInfoVO;

/**
 * 机器信息 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:10
 */
public class MachineConversion {

    static {
        TypeStore.STORE.register(MachineInfoDO.class, MachineInfoVO.class, p -> {
            MachineInfoVO vo = new MachineInfoVO();
            vo.setId(p.getId());
            vo.setProxyId(p.getProxyId());
            vo.setKeyId(p.getKeyId());
            vo.setHost(p.getMachineHost());
            vo.setSshPort(p.getSshPort());
            vo.setName(p.getMachineName());
            vo.setTag(p.getMachineTag());
            vo.setDescription(p.getDescription());
            vo.setUsername(p.getUsername());
            vo.setAuthType(p.getAuthType());
            vo.setStatus(p.getMachineStatus());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(MachineInfoRequest.class, MachineInfoDO.class, p -> {
            MachineInfoDO d = new MachineInfoDO();
            d.setId(p.getId());
            d.setProxyId(p.getProxyId());
            d.setKeyId(p.getKeyId());
            d.setMachineHost(p.getHost());
            d.setSshPort(p.getSshPort());
            d.setMachineName(p.getName());
            d.setMachineTag(p.getTag());
            d.setDescription(p.getDescription());
            d.setUsername(p.getUsername());
            d.setAuthType(p.getAuthType());
            d.setMachineStatus(p.getStatus());
            return d;
        });
    }

}
