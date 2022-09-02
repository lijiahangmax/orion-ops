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
