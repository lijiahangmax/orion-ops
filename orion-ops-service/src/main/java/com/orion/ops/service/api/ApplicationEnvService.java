package com.orion.ops.service.api;

import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.domain.ApplicationEnvDO;
import com.orion.ops.entity.request.ApplicationConfigRequest;
import com.orion.ops.entity.request.ApplicationEnvRequest;
import com.orion.ops.entity.vo.ApplicationEnvVO;

import java.util.List;
import java.util.Map;

/**
 * 应用环境api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/4 11:23
 */
public interface ApplicationEnvService {

    /**
     * 添加应用变量
     *
     * @param request request
     * @return id
     */
    Long addAppEnv(ApplicationEnvRequest request);

    /**
     * 更新应用变量
     *
     * @param request request
     * @return effect
     */
    Integer updateAppEnv(ApplicationEnvRequest request);

    /**
     * 更新应用变量
     *
     * @param before 数据
     * @param update update
     * @return effect
     */
    Integer updateAppEnv(ApplicationEnvDO before, ApplicationEnvRequest update);

    /**
     * 删除应用变量
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteAppEnv(List<Long> idList);

    /**
     * 保存应用变量
     *
     * @param appId     appId
     * @param profileId profileId
     * @param env       env
     */
    void saveEnv(Long appId, Long profileId, Map<String, String> env);

    /**
     * 应用环境变量列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<ApplicationEnvVO> listAppEnv(ApplicationEnvRequest request);

    /**
     * 应用环境变量详情
     *
     * @param id id
     * @return row
     */
    ApplicationEnvVO getAppEnvDetail(Long id);

    /**
     * 同步应用环境变量到其他环境
     *
     * @param id                  id
     * @param appId               appId
     * @param profileId           profileId
     * @param targetProfileIdList 同步环境id
     */
    void syncAppEnv(Long id, Long appId, Long profileId, List<Long> targetProfileIdList);

    /**
     * 获取环境变量值
     *
     * @param appId     appId
     * @param profileId profileId
     * @param key       key
     * @return value
     */
    String getAppEnvValue(Long appId, Long profileId, String key);

    /**
     * 获取应用环境变量
     *
     * @param appId     appId
     * @param profileId profileId
     * @return map
     */
    MutableLinkedHashMap<String, String> getAppProfileEnv(Long appId, Long profileId);

    /**
     * 通过appId profileId 删除env
     *
     * @param appId     appId
     * @param profileId profileId
     * @return effect
     */
    Integer deleteAppProfileEnvByAppProfileId(Long appId, Long profileId);

    /**
     * 配置app环境
     *
     * @param request request
     */
    void configAppEnv(ApplicationConfigRequest request);

    /**
     * 同步app环境变量
     *
     * @param appId         appId
     * @param profileId     profileId
     * @param syncProfileId 需要同步的profileId
     */
    void syncAppProfileEnv(Long appId, Long profileId, Long syncProfileId);

    /**
     * 复制环境变量
     *
     * @param appId       appId
     * @param targetAppId targetAppId
     */
    void copyAppEnv(Long appId, Long targetAppId);

}
