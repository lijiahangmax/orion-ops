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
package cn.orionsec.ops.mapping.scheduler;

import cn.orionsec.ops.entity.domain.SchedulerTaskDO;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskVO;
import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.cron.Cron;
import com.orion.lang.utils.time.cron.CronSupport;

/**
 * 定时任务配置 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:18
 */
public class SchedulerTaskConversion {

    static {
        TypeStore.STORE.register(SchedulerTaskDO.class, SchedulerTaskVO.class, p -> {
            SchedulerTaskVO vo = new SchedulerTaskVO();
            vo.setId(p.getId());
            vo.setName(p.getTaskName());
            vo.setDescription(p.getDescription());
            vo.setCommand(p.getTaskCommand());
            vo.setExpression(p.getExpression());
            vo.setEnableStatus(p.getEnableStatus());
            vo.setLatelyStatus(p.getLatelyStatus());
            vo.setSerializeType(p.getSerializeType());
            vo.setExceptionHandler(p.getExceptionHandler());
            vo.setLatelyScheduleTime(p.getLatelyScheduleTime());
            vo.setUpdateTime(p.getUpdateTime());
            try {
                vo.setNextTime(CronSupport.getNextTime(new Cron(p.getExpression()), 5));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return vo;
        });
    }

}
