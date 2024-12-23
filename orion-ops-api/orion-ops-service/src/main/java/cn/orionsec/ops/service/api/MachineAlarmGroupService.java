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

import cn.orionsec.ops.entity.domain.MachineAlarmGroupDO;

import java.util.List;

/**
 * 机器报警组服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 17:53
 */
public interface MachineAlarmGroupService {

    /**
     * 通过机器id查询
     *
     * @param machineId machineId
     * @return rows
     */
    List<MachineAlarmGroupDO> selectByMachineId(Long machineId);

    /**
     * 通过机器id删除
     *
     * @param machineId machineId
     * @return effect
     */
    Integer deleteByMachineId(Long machineId);

    /**
     * 通过机器id删除
     *
     * @param machineIdList machineIdList
     * @return effect
     */
    Integer deleteByMachineIdList(List<Long> machineIdList);

    /**
     * 通过报警组id删除
     *
     * @param groupId groupId
     * @return effect
     */
    Integer deleteByGroupId(Long groupId);

}
