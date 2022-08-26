package com.orion.ops.mapping.alarm;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.AlarmGroupDO;
import com.orion.ops.entity.domain.AlarmGroupUserDO;
import com.orion.ops.entity.vo.alarm.AlarmGroupUserVO;
import com.orion.ops.entity.vo.alarm.AlarmGroupVO;

/**
 * 报警组 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/25 19:04
 */
public class AlarmGroupConversion {

    static {
        TypeStore.STORE.register(AlarmGroupDO.class, AlarmGroupVO.class, p -> {
            AlarmGroupVO vo = new AlarmGroupVO();
            vo.setId(p.getId());
            vo.setName(p.getGroupName());
            vo.setDescription(p.getGroupDescription());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(AlarmGroupUserDO.class, AlarmGroupUserVO.class, p -> {
            AlarmGroupUserVO vo = new AlarmGroupUserVO();
            vo.setId(p.getId());
            vo.setUserId(p.getUserId());
            vo.setUsername(p.getUsername());
            return vo;
        });
    }

}
