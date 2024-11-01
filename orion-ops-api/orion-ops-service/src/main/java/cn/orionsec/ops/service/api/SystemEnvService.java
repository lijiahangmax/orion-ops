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

import cn.orionsec.kit.lang.define.collect.MutableLinkedHashMap;
import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.ops.entity.domain.SystemEnvDO;
import cn.orionsec.ops.entity.request.system.SystemEnvRequest;
import cn.orionsec.ops.entity.vo.system.SystemEnvVO;

import java.util.List;
import java.util.Map;

/**
 * 系统环境变量服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 17:05
 */
public interface SystemEnvService {

    /**
     * 添加变量
     *
     * @param request request
     * @return id
     */
    Long addEnv(SystemEnvRequest request);

    /**
     * 修改变量
     *
     * @param request request
     * @return effect
     */
    Integer updateEnv(SystemEnvRequest request);

    /**
     * 修改变量
     *
     * @param before  before
     * @param request request
     * @return effect
     */
    Integer updateEnv(SystemEnvDO before, SystemEnvRequest request);

    /**
     * 通过id删除
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteEnv(List<Long> idList);

    /**
     * 批量添加
     *
     * @param env env
     */
    void saveEnv(Map<String, String> env);

    /**
     * 列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<SystemEnvVO> listEnv(SystemEnvRequest request);

    /**
     * 详情
     *
     * @param id id
     * @return row
     */
    SystemEnvVO getEnvDetail(Long id);

    /**
     * 获取系统变量
     *
     * @param env envKey
     * @return env
     */
    String getEnvValue(String env);

    /**
     * 通过名称获取
     *
     * @param env env
     * @return env
     */
    SystemEnvDO selectByName(String env);

    /**
     * 获取系统环境变量
     *
     * @return map
     */
    MutableLinkedHashMap<String, String> getSystemEnv();

    /**
     * 获取系统环境变量
     *
     * @return map
     */
    MutableLinkedHashMap<String, String> getFullSystemEnv();

}
