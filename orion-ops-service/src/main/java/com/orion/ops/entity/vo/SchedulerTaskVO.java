package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.SchedulerTaskDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.cron.Cron;
import com.orion.utils.time.cron.CronSupport;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 调度任务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/22 11:19
 */
@Data
public class SchedulerTaskVO {

    /**
     * id
     */
    private Long id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 执行命令
     */
    private String command;

    /**
     * cron表达式
     */
    private String expression;

    /**
     * 启用状态 1启用 2停用
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    private Integer enableStatus;

    /**
     * 最近状态 10待调度 20调度中 30调度成功 40调度失败 50已停止
     *
     * @see com.orion.ops.consts.scheduler.SchedulerTaskStatus
     */
    private Integer latelyStatus;

    /**
     * 调度序列 10串行 20并行
     *
     * @see com.orion.ops.consts.SerialType
     */
    private Integer serializeType;

    /**
     * 异常处理 10跳过所有 20跳过错误
     *
     * @see com.orion.ops.consts.ExceptionHandlerType
     */
    private Integer exceptionHandler;

    /**
     * 上次调度时间
     */
    private Date latelyScheduleTime;

    /**
     * 执行机器id
     */
    private List<Long> machineIdList;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 下次执行时间
     */
    private List<Date> nextTime;

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
