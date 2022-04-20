package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.request.SchedulerTaskRecordRequest;
import com.orion.ops.entity.vo.SchedulerTaskMachineRecordStatusVO;
import com.orion.ops.entity.vo.SchedulerTaskMachineRecordVO;
import com.orion.ops.entity.vo.SchedulerTaskRecordStatusVO;
import com.orion.ops.entity.vo.SchedulerTaskRecordVO;

import java.util.List;

/**
 * <p>
 * 调度任务执行日志 服务类
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
public interface SchedulerTaskRecordService {

    /**
     * 通过 taskId 删除
     *
     * @param taskId taskId
     * @return effect
     */
    Integer deleteByTaskId(Long taskId);

    /**
     * 创建调度明细
     *
     * @param taskId taskId
     * @return recordId
     */
    Long createTaskRecord(Long taskId);

    /**
     * 查询任务明细
     *
     * @param request request
     * @return rows
     */
    DataGrid<SchedulerTaskRecordVO> listTaskRecord(SchedulerTaskRecordRequest request);

    /**
     * 通过 id 查询
     *
     * @param id id
     * @return row
     */
    SchedulerTaskRecordVO getDetailById(Long id);

    /**
     * 查询任务机器明细
     *
     * @param recordId recordId
     * @return rows
     */
    List<SchedulerTaskMachineRecordVO> listMachinesRecord(Long recordId);

    /**
     * 查询任务状态
     *
     * @param idList              idList
     * @param machineRecordIdList machineRecordIdList
     * @return status
     */
    List<SchedulerTaskRecordStatusVO> listRecordStatus(List<Long> idList, List<Long> machineRecordIdList);

    /**
     * 查询任务机器状态
     *
     * @param idList id
     * @return status
     */
    List<SchedulerTaskMachineRecordStatusVO> listMachineRecordStatus(List<Long> idList);

    /**
     * 删除调度明细
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteTaskRecord(List<Long> idList);

    /**
     * 停止所有
     *
     * @param id id
     */
    void terminateAll(Long id);

    /**
     * 停止单个
     *
     * @param id              id
     * @param machineRecordId machineRecordId
     */
    void terminateMachine(Long id, Long machineRecordId);

    /**
     * 跳过单个
     *
     * @param id              id
     * @param machineRecordId machineRecordId
     */
    void skipMachine(Long id, Long machineRecordId);

}
