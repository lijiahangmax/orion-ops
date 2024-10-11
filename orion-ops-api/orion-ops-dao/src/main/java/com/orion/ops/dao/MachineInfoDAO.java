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
package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.MachineInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 机器信息表 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-03-29
 */
public interface MachineInfoDAO extends BaseMapper<MachineInfoDO> {

    /**
     * 设置id
     *
     * @param oldId 原id
     * @param newId 新id
     */
    void setId(@Param("oldId") Long oldId, @Param("newId") Long newId);

    /**
     * 设置proxyId为null
     *
     * @param proxyId proxyId
     */
    void setProxyIdWithNull(@Param("proxyId") Long proxyId);

    /**
     * 通过host查询id
     *
     * @param host host
     * @return rows
     */
    List<Long> selectIdByHost(@Param("host") String host);

    /**
     * 查询机器名称
     *
     * @param id id
     * @return name
     */
    String selectMachineName(@Param("id") Long id);

    /**
     * 查询机器名称
     *
     * @param idList idList
     * @return rows
     */
    List<MachineInfoDO> selectNameByIdList(@Param("idList") List<Long> idList);

    /**
     * 查询机器id
     *
     * @param tagList tagList
     * @return rows
     */
    List<MachineInfoDO> selectIdByTagList(@Param("tagList") List<String> tagList);

}
