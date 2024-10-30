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

import cn.orionsec.ops.entity.domain.ApplicationActionDO;
import cn.orionsec.ops.entity.request.app.ApplicationConfigRequest;

import java.util.List;
import java.util.Map;

/**
 * app发布流程接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/8 18:30
 */
public interface ApplicationActionService {

    /**
     * 删除发布流程
     *
     * @param appId     appId
     * @param profileId profileId
     * @return effect
     */
    Integer deleteAppActionByAppProfileId(Long appId, Long profileId);

    /**
     * 获取发布流程
     *
     * @param appId     appId
     * @param profileId profileId
     * @param stageType stageType
     * @return actions
     */
    List<ApplicationActionDO> getAppProfileActions(Long appId, Long profileId, Integer stageType);

    /**
     * 通过appId profileId查询操作流程数量数量
     *
     * @param appId     appId
     * @param profileId profileId
     * @param stageType stageType
     * @return count
     */
    Integer getAppProfileActionCount(Long appId, Long profileId, Integer stageType);

    /**
     * 配置app发布流程
     *
     * @param request request
     */
    void configAppAction(ApplicationConfigRequest request);

    /**
     * 同步app操作流程
     *
     * @param appId         appId
     * @param profileId     profileId
     * @param syncProfileId 需要同步的profileId
     */
    void syncAppProfileAction(Long appId, Long profileId, Long syncProfileId);

    /**
     * 复制发布流程
     *
     * @param appId       appId
     * @param targetAppId targetAppId
     */
    void copyAppAction(Long appId, Long targetAppId);

    /**
     * 获取app是否已配置
     *
     * @param profileId profileId
     * @param appIdList appIdList
     * @return appId, isConfig
     */
    Map<Long, Boolean> getAppIsConfig(Long profileId, List<Long> appIdList);

}
