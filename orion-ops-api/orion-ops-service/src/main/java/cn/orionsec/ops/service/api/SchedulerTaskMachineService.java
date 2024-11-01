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

import cn.orionsec.ops.entity.domain.SchedulerTaskMachineDO;

import java.util.List;

/**
 * <p>
 * 调度任务机器 服务类
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
public interface SchedulerTaskMachineService {

    /**
     * 通过 taskId 查询
     *
     * @param taskId taskId
     * @return rows
     */
    List<SchedulerTaskMachineDO> selectByTaskId(Long taskId);

    /**
     * 通过 taskId 删除
     *
     * @param taskId taskId
     * @return effect
     */
    Integer deleteByTaskId(Long taskId);

    /**
     * 通过 机器id 删除
     *
     * @param machineIdList machineIdList
     * @return effect
     */
    Integer deleteByMachineIdList(List<Long> machineIdList);

}
