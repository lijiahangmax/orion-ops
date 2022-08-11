package com.orion.ops.service.api;

import com.orion.ops.entity.domain.ApplicationActionDO;
import com.orion.ops.entity.request.app.ApplicationConfigRequest;

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
