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
package com.orion.ops.mapping.machine;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.MachineAlarmConfigDO;
import com.orion.ops.entity.domain.MachineAlarmHistoryDO;
import com.orion.ops.entity.vo.machine.MachineAlarmConfigVO;
import com.orion.ops.entity.vo.machine.MachineAlarmHistoryVO;

/**
 * 机器报警 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 17:51
 */
public class MachineAlarmConversion {

    static {
        TypeStore.STORE.register(MachineAlarmConfigDO.class, MachineAlarmConfigVO.class, p -> {
            MachineAlarmConfigVO vo = new MachineAlarmConfigVO();
            vo.setType(p.getAlarmType());
            vo.setAlarmThreshold(p.getAlarmThreshold());
            vo.setTriggerThreshold(p.getTriggerThreshold());
            vo.setNotifySilence(p.getNotifySilence());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(MachineAlarmHistoryDO.class, MachineAlarmHistoryVO.class, p -> {
            MachineAlarmHistoryVO vo = new MachineAlarmHistoryVO();
            vo.setId(p.getId());
            vo.setType(p.getAlarmType());
            vo.setAlarmValue(p.getAlarmValue());
            vo.setAlarmTime(p.getAlarmTime());
            vo.setAlarmTimeAgo(Dates.ago(p.getAlarmTime()));
            return vo;
        });
    }

}
