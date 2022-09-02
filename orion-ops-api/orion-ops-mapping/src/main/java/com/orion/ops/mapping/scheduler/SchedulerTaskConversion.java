package com.orion.ops.mapping.scheduler;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.cron.Cron;
import com.orion.lang.utils.time.cron.CronSupport;
import com.orion.ops.entity.domain.SchedulerTaskDO;
import com.orion.ops.entity.vo.scheduler.SchedulerTaskVO;

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
