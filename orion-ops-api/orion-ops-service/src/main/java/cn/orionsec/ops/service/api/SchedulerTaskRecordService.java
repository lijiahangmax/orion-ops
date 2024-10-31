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
package cn.orionsec.ops.service.api;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.ops.entity.request.scheduler.SchedulerTaskRecordRequest;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskMachineRecordStatusVO;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskMachineRecordVO;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskRecordStatusVO;
import cn.orionsec.ops.entity.vo.scheduler.SchedulerTaskRecordVO;

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
     * @param machineRecordId machineRecordId
     */
    void terminateMachine(Long machineRecordId);

    /**
     * 跳过单个
     *
     * @param machineRecordId machineRecordId
     */
    void skipMachine(Long machineRecordId);

    /**
     * 发送命令
     *
     * @param machineRecordId machineRecordId
     * @param command         command
     */
    void writeMachine(Long machineRecordId, String command);

}
