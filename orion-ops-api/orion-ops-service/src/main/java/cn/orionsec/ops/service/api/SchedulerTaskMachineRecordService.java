/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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

import cn.orionsec.ops.entity.domain.SchedulerTaskMachineRecordDO;

import java.util.List;

/**
 * <p>
 * 调度任务执行明细机器详情 服务类
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
public interface SchedulerTaskMachineRecordService {

    /**
     * 通过 taskId 删除
     *
     * @param taskId taskId
     * @return effect
     */
    Integer deleteByTaskId(Long taskId);

    /**
     * 通过 recordId 查询机器明细
     *
     * @param recordId recordId
     * @return rows
     */
    List<SchedulerTaskMachineRecordDO> selectByRecordId(Long recordId);

    /**
     * 获取任务机器执行日志路径
     *
     * @param machineRecordId machineRecordId
     * @return path
     */
    String getTaskMachineLogPath(Long machineRecordId);

}
