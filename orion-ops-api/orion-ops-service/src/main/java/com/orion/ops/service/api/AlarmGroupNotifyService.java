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

import com.orion.ops.entity.domain.AlarmGroupNotifyDO;

import java.util.List;

/**
 * 报警组通知方式服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 10:29
 */
public interface AlarmGroupNotifyService {

    /**
     * 通过 groupId 查询
     *
     * @param groupId groupId
     * @return rows
     */
    List<AlarmGroupNotifyDO> selectByGroupId(Long groupId);

    /**
     * 通过 groupId 查询
     *
     * @param groupIdList groupIdList
     * @return rows
     */
    List<AlarmGroupNotifyDO> selectByGroupIdList(List<Long> groupIdList);

    /**
     * 通过 groupId 删除
     *
     * @param groupId groupId
     * @return effect
     */
    Integer deleteByGroupId(Long groupId);

    /**
     * 通过 webhookId 删除
     *
     * @param webhookId webhookId
     * @return effect
     */
    Integer deleteByWebhookId(Long webhookId);

}
