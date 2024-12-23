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

import cn.orionsec.ops.entity.domain.ApplicationReleaseMachineDO;

import java.util.List;

/**
 * <p>
 * 发布任务机器表 服务类
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
public interface ApplicationReleaseMachineService {

    /**
     * 查询发布机器
     *
     * @param releaseId releaseId
     * @return rows
     */
    List<ApplicationReleaseMachineDO> getReleaseMachines(Long releaseId);

    /**
     * 查询发布机器
     *
     * @param releaseIdList releaseIdList
     * @return rows
     */
    List<ApplicationReleaseMachineDO> getReleaseMachines(List<Long> releaseIdList);

    /**
     * 查询发布机器id
     *
     * @param releaseIdList releaseIdList
     * @return rows
     */
    List<Long> getReleaseMachineIdList(List<Long> releaseIdList);

    /**
     * 通过 releaseId 删除
     *
     * @param releaseId releaseId
     * @return effect
     */
    Integer deleteByReleaseId(Long releaseId);

    /**
     * 获取发布机器日志
     *
     * @param id id
     * @return 日志路径
     */
    String getReleaseMachineLogPath(Long id);

}
