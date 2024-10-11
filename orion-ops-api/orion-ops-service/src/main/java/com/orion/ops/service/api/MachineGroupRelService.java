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

import java.util.List;
import java.util.Map;

/**
 * 机器分组关联服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/23 16:10
 */
public interface MachineGroupRelService {

    /**
     * 组内添加机器 一组多机器 (分组视图添加/修改机器时)
     *
     * @param groupId       groupId
     * @param machineIdList machineIdList
     */
    void addMachineRelByGroup(Long groupId, List<Long> machineIdList);

    /**
     * 修改机器分组 一机器多组 (新增/修改机器时)
     *
     * @param machineId   machineId
     * @param groupIdList groupIdList
     */
    void updateMachineGroup(Long machineId, List<Long> groupIdList);

    /**
     * 组内删除机器
     *
     * @param groupIdList   groupIdList
     * @param machineIdList machineIdList
     * @return effect
     */
    Integer deleteByGroupMachineId(List<Long> groupIdList, List<Long> machineIdList);

    /**
     * 通过机器id删除
     *
     * @param machineIdList machineIdList
     * @return effect
     */
    Integer deleteByMachineIdList(List<Long> machineIdList);

    /**
     * 通过分组id删除
     *
     * @param groupIdList groupIdList
     * @return effect
     */
    Integer deleteByGroupIdList(List<Long> groupIdList);

    /**
     * 获取机器分组引用缓存
     *
     * @return cache
     */
    Map<Long, List<Long>> getMachineRelByCache();

    /**
     * 清理分组机器引用缓存
     */
    void clearGroupRelCache();

}
