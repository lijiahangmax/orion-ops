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
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.machine.MachineEnvAttr;
import cn.orionsec.ops.entity.domain.MachineEnvDO;
import cn.orionsec.ops.entity.vo.machine.MachineEnvVO;

/**
 * 机器环境变量 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:12
 */
public class MachineEnvConversion {

    static {
        TypeStore.STORE.register(MachineEnvDO.class, MachineEnvVO.class, p -> {
            MachineEnvVO vo = new MachineEnvVO();
            vo.setId(p.getId());
            vo.setMachineId(p.getMachineId());
            vo.setKey(p.getAttrKey());
            vo.setValue(p.getAttrValue());
            vo.setDescription(p.getDescription());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            Integer forbidDelete = MachineEnvAttr.of(p.getAttrKey()) == null ? Const.FORBID_DELETE_CAN : Const.FORBID_DELETE_NOT;
            vo.setForbidDelete(forbidDelete);
            return vo;
        });
    }

}
