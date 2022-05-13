package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 任务执行记录
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/25 14:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SchedulerTaskRecordRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * idList
     */
    private List<Long> idList;

    /**
     * 机器明细idList
     */
    private List<Long> machineRecordIdList;

    /**
     * 明细id
     */
    private Long recordId;

    /**
     * 机器明细id
     */
    private Long machineRecordId;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 状态
     *
     * @see com.orion.ops.consts.scheduler.SchedulerTaskStatus
     */
    private Integer status;

    /**
     * 命令
     */
    private String command;

    /**
     * 是否发送 \n
     */
    private boolean sendLf;

}
