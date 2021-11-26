package com.orion.ops.service.api;

import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.lang.wrapper.DataGrid;
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
     * 批量添加应用变量
     *
     * @param appId     appId
     * @param profileId profileId
     * @param env       env
     */
    void batchAddAppEnv(Long appId, Long profileId, Map<String, String> env);

    /**
     * 删除应用变量
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteAppEnv(List<Long> idList);

    /**
     * 更新应用变量
     *
     * @param request request
     * @return effect
     */
    Integer updateAppEnv(ApplicationEnvRequest request);

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
     * @param envKey    key
     * @return effect
     */
    Integer deleteAppProfileEnvByAppProfileId(Long appId, Long profileId, Object... envKey);

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
