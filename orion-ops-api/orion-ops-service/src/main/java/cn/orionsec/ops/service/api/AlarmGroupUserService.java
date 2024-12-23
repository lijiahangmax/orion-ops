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

import cn.orionsec.ops.entity.domain.AlarmGroupUserDO;

import java.util.List;

/**
 * 报警组员服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/26 10:29
 */
public interface AlarmGroupUserService {

    /**
     * 通过 groupId 查询
     *
     * @param groupId groupId
     * @return rows
     */
    List<AlarmGroupUserDO> selectByGroupId(Long groupId);

    /**
     * 通过 groupId 查询
     *
     * @param groupIdList groupIdList
     * @return rows
     */
    List<AlarmGroupUserDO> selectByGroupIdList(List<Long> groupIdList);

    /**
     * 通过 groupId 删除
     *
     * @param groupId groupId
     * @return effect
     */
    Integer deleteByGroupId(Long groupId);

    /**
     * 通过 userId 删除
     *
     * @param userId userId
     * @return effect
     */
    Integer deleteByUserId(Long userId);

}
