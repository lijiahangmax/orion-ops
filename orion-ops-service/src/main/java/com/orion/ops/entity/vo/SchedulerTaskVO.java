package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.cron.Cron;
import com.orion.lang.utils.time.cron.CronSupport;
import com.orion.ops.entity.domain.SchedulerTaskDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 调度任务响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/22 11:19
 */
@Data
@ApiModel(value = "调度任务响应")
public class SchedulerTaskVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "任务描述")
    private String description;

    @ApiModelProperty(value = "执行命令")
    private String command;

    @ApiModelProperty(value = "cron表达式")
    private String expression;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "启用状态 1启用 2停用")
    private Integer enableStatus;

    /**
     * @see com.orion.ops.constant.scheduler.SchedulerTaskStatus
     */
    @ApiModelProperty(value = "最近状态 10待调度 20调度中 30调度成功 40调度失败 50已停止")
    private Integer latelyStatus;

    /**
     * @see com.orion.ops.constant.SerialType
     */
    @ApiModelProperty(value = "调度序列 10串行 20并行")
    private Integer serializeType;

    /**
     * @see com.orion.ops.constant.ExceptionHandlerType
     */
    @ApiModelProperty(value = "异常处理 10跳过所有 20跳过错误")
    private Integer exceptionHandler;

    @ApiModelProperty(value = "上次调度时间")
    private Date latelyScheduleTime;

    @ApiModelProperty(value = "执行机器id")
    private List<Long> machineIdList;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "下次执行时间")
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
