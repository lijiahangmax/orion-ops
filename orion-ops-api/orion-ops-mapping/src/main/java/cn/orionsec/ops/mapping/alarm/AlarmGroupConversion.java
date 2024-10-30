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
package cn.orionsec.ops.mapping.alarm;

import cn.orionsec.ops.entity.domain.AlarmGroupDO;
import cn.orionsec.ops.entity.domain.AlarmGroupUserDO;
import cn.orionsec.ops.entity.vo.alarm.AlarmGroupUserVO;
import cn.orionsec.ops.entity.vo.alarm.AlarmGroupVO;
import com.orion.lang.utils.convert.TypeStore;

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
