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

import cn.orionsec.ops.constant.app.StageType;
import cn.orionsec.ops.entity.domain.ApplicationActionLogDO;
import cn.orionsec.ops.entity.vo.app.ApplicationActionLogVO;

import java.util.List;

/**
 * 应用操作日志服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/11 16:41
 */
public interface ApplicationActionLogService {

    /**
     * 通过 id 查询详情
     *
     * @param id id
     * @return detail
     */
    ApplicationActionLogVO getDetailById(Long id);

    /**
     * 通过 id 查询状态
     *
     * @param id id
     * @return detail
     */
    ApplicationActionLogVO getStatusById(Long id);

    /**
     * 获取操作执行日志
     *
     * @param relId     relId
     * @param stageType stageType
     * @return rows
     */
    List<ApplicationActionLogVO> getActionLogsByRelId(Long relId, StageType stageType);

    /**
     * 删除
     *
     * @param relId     relId
     * @param stageType stageType
     * @return effect
     */
    Integer deleteByRelId(Long relId, StageType stageType);

    /**
     * 删除
     *
     * @param relIdList relIdList
     * @param stageType stageType
     * @return effect
     */
    Integer deleteByRelIdList(List<Long> relIdList, StageType stageType);

    /**
     * 通过 relId 查询 action
     *
     * @param relId     relId
     * @param stageType stageType
     * @return action
     */
    List<ApplicationActionLogDO> selectActionByRelId(Long relId, StageType stageType);

    /**
     * 通过 relIdList 查询 action
     *
     * @param relIdList relIdList
     * @param stageType stageType
     * @return action
     */
    List<ApplicationActionLogDO> selectActionByRelIdList(List<Long> relIdList, StageType stageType);

    /**
     * 更新 action
     *
     * @param record record
     */
    void updateActionById(ApplicationActionLogDO record);

    /**
     * 获取操作执行日志路径
     *
     * @param id id
     * @return path
     */
    String getActionLogPath(Long id);

    /**
     * 设置操作状态
     *
     * @param relId     relId
     * @param stageType stageType
     */
    void resetActionStatus(Long relId, StageType stageType);

}
