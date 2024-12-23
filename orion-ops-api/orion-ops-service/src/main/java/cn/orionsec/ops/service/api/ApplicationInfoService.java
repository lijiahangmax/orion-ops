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

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.ops.entity.request.app.ApplicationConfigRequest;
import cn.orionsec.ops.entity.request.app.ApplicationInfoRequest;
import cn.orionsec.ops.entity.vo.app.ApplicationDetailVO;
import cn.orionsec.ops.entity.vo.app.ApplicationInfoVO;
import cn.orionsec.ops.entity.vo.app.ApplicationMachineVO;

import java.util.List;

/**
 * 应用服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 17:52
 */
public interface ApplicationInfoService {

    /**
     * 添加应用
     *
     * @param request request
     * @return id
     */
    Long insertApp(ApplicationInfoRequest request);

    /**
     * 更新应用
     *
     * @param request request
     * @return effect
     */
    Integer updateApp(ApplicationInfoRequest request);

    /**
     * 更新排序
     *
     * @param id        id
     * @param increment 是否为上调
     * @return effect
     */
    Integer updateAppSort(Long id, boolean increment);

    /**
     * 删除应用
     *
     * @param id id
     * @return effect
     */
    Integer deleteApp(Long id);

    /**
     * 应用列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<ApplicationInfoVO> listApp(ApplicationInfoRequest request);

    /**
     * 获取应用机器
     *
     * @param appId     appId
     * @param profileId profileId
     * @return machines
     */
    List<ApplicationMachineVO> getAppMachines(Long appId, Long profileId);

    /**
     * 获取应用详情
     *
     * @param appId     appId
     * @param profileId profileId
     * @return detail
     */
    ApplicationDetailVO getAppDetail(Long appId, Long profileId);

    /**
     * 配置应用环境配置
     *
     * @param request request
     */
    void configAppProfile(ApplicationConfigRequest request);

    /**
     * 同步应用环境配置
     *
     * @param appId             appId
     * @param profileId         profileId
     * @param targetProfileList targetProfileList
     */
    void syncAppProfileConfig(Long appId, Long profileId, List<Long> targetProfileList);

    /**
     * 复制应用
     *
     * @param appId appId
     */
    void copyApplication(Long appId);

    /**
     * 检测应用是否已经配置
     *
     * @param appId     appId
     * @param profileId profileId
     * @return 检测应用是否已经配置
     */
    boolean checkAppConfig(Long appId, Long profileId);

    /**
     * 获取下一个排序
     *
     * @return sort
     */
    Integer getNextSort();

}
