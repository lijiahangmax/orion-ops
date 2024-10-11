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

import com.orion.lang.define.collect.MutableLinkedHashMap;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.entity.domain.ApplicationEnvDO;
import com.orion.ops.entity.request.app.ApplicationConfigRequest;
import com.orion.ops.entity.request.app.ApplicationEnvRequest;
import com.orion.ops.entity.vo.app.ApplicationEnvVO;

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
     * 获取应用环境变量 包含应用 环境
     *
     * @param appId     appId
     * @param profileId profileId
     * @return env
     */
    MutableLinkedHashMap<String, String> getAppProfileFullEnv(Long appId, Long profileId);

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
     * 复制环境变量
     *
     * @param appId       appId
     * @param targetAppId targetAppId
     */
    void copyAppEnv(Long appId, Long targetAppId);

    /**
     * 获取构建seq
     *
     * @param appId     appId
     * @param profileId profileId
     * @return seq
     */
    Integer getBuildSeqAndIncrement(Long appId, Long profileId);

    /**
     * 检查并初始化系统变量
     *
     * @param appId     appId
     * @param profileId profileId
     */
    void checkInitSystemEnv(Long appId, Long profileId);

}
