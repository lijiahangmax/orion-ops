package com.orion.ops.service.api;

import com.orion.ops.entity.domain.ApplicationActionDO;
import com.orion.ops.entity.request.ApplicationConfigRequest;

import java.util.List;

/**
 * app发布步骤接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/8 18:30
 */
public interface ApplicationActionService {

    /**
     * 删除发布步骤
     *
     * @param appId     appId
     * @param profileId profileId
     * @return effect
     */
    Integer deleteAppActionByAppProfileId(Long appId, Long profileId);

    /**
     * 获取发布步骤
     *
     * @param appId     appId
     * @param profileId profileId
     * @return actions
     */
    List<ApplicationActionDO> getAppProfileActions(Long appId, Long profileId);

    /**
     * 通过appId profileId查询操作步骤数量数量
     *
     * @param appId     appId
     * @param profileId profileId
     * @param stageType stageType
     * @return count
     */
    Integer getAppProfileActionCount(Long appId, Long profileId, Integer stageType);

    /**
     * 配置app发布步骤
     *
     * @param request request
     */
    void configAppAction(ApplicationConfigRequest request);

    /**
     * 同步app操作步骤
     *
     * @param appId         appId
     * @param profileId     profileId
     * @param syncProfileId 需要同步的profileId
     */
    void syncAppProfileAction(Long appId, Long profileId, Long syncProfileId);

    /**
     * 复制发布步骤
     *
     * @param appId       appId
     * @param targetAppId targetAppId
     */
    void copyAppAction(Long appId, Long targetAppId);

}
