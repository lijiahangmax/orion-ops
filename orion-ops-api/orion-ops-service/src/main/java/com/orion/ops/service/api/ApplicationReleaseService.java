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
package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.entity.request.app.ApplicationReleaseAuditRequest;
import com.orion.ops.entity.request.app.ApplicationReleaseRequest;
import com.orion.ops.entity.vo.app.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 发布任务 服务类
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
public interface ApplicationReleaseService {

    /**
     * 发布列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<ApplicationReleaseListVO> getReleaseList(ApplicationReleaseRequest request);

    /**
     * 查询发布机器列表
     *
     * @param id id
     * @return rows
     */
    List<ApplicationReleaseMachineVO> getReleaseMachineList(Long id);

    /**
     * 发布详情
     *
     * @param request request
     * @return row
     */
    ApplicationReleaseDetailVO getReleaseDetail(ApplicationReleaseRequest request);

    /**
     * 获取发布机器详情
     *
     * @param releaseMachineId id
     * @return row
     */
    ApplicationReleaseMachineVO getReleaseMachineDetail(Long releaseMachineId);

    /**
     * 提交
     *
     * @param request request
     * @return id
     */
    Long submitAppRelease(ApplicationReleaseRequest request);

    /**
     * 复制
     *
     * @param id id
     * @return id
     */
    Long copyAppRelease(Long id);

    /**
     * 审核
     *
     * @param request request
     * @return effect
     */
    Integer auditAppRelease(ApplicationReleaseAuditRequest request);

    /**
     * 执行发布
     *
     * @param id             id
     * @param systemSchedule 是否为系统调度
     * @param execute        是否立即执行
     */
    void runnableAppRelease(Long id, boolean systemSchedule, boolean execute);

    /**
     * 取消执行
     *
     * @param id id
     */
    void cancelAppTimedRelease(Long id);

    /**
     * 设置定时时间
     *
     * @param id          id
     * @param releaseTime 定时时间
     */
    void setTimedRelease(Long id, Date releaseTime);

    /**
     * 回滚
     *
     * @param id id
     * @return id
     */
    Long rollbackAppRelease(Long id);

    /**
     * 终止
     *
     * @param id id
     */
    void terminateRelease(Long id);

    /**
     * 终止
     *
     * @param releaseMachineId releaseMachineId
     */
    void terminateMachine(Long releaseMachineId);

    /**
     * 跳过
     *
     * @param releaseMachineId releaseMachineId
     */
    void skipMachine(Long releaseMachineId);

    /**
     * 输入命令
     *
     * @param releaseMachineId releaseMachineId
     * @param command          命令
     */
    void writeMachine(Long releaseMachineId, String command);

    /**
     * 删除
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteRelease(List<Long> idList);

    /**
     * 获取发布状态列表
     *
     * @param idList        idList
     * @param machineIdList machineIdList
     * @return list
     */
    List<ApplicationReleaseStatusVO> getReleaseStatusList(List<Long> idList, List<Long> machineIdList);

    /**
     * 获取发布状态
     *
     * @param id id
     * @return status
     */
    ApplicationReleaseStatusVO getReleaseStatus(Long id);

    /**
     * 获取发布状态机器列表
     *
     * @param releaseMachineIdList releaseMachineIdList
     * @return list
     */
    List<ApplicationReleaseMachineStatusVO> getReleaseMachineStatusList(List<Long> releaseMachineIdList);

    /**
     * 获取发布机器状态
     *
     * @param releaseMachineId releaseMachineId
     * @return status
     */
    ApplicationReleaseMachineStatusVO getReleaseMachineStatus(Long releaseMachineId);

}
