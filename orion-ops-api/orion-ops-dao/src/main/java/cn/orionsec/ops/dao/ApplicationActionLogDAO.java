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
package cn.orionsec.ops.dao;

import cn.orionsec.ops.entity.domain.ApplicationActionLogDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用操作日志 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-11
 */
public interface ApplicationActionLogDAO extends BaseMapper<ApplicationActionLogDO> {

    /**
     * 通过 id 查询状态信息
     *
     * @param id id
     * @return row
     */
    ApplicationActionLogDO selectStatusInfoById(@Param("id") Long id);

    /**
     * 通过 id 查询状态信息
     *
     * @param idList idList
     * @return rows
     */
    List<ApplicationActionLogDO> selectStatusInfoByIdList(@Param("idList") List<Long> idList);

    /**
     * 通过 relId 查询状态信息
     *
     * @param relId     relId
     * @param stageType stageType
     * @return rows
     */
    List<ApplicationActionLogDO> selectStatusInfoByRelId(@Param("relId") Long relId, @Param("stageType") Integer stageType);

    /**
     * 通过 relId 查询状态信息
     *
     * @param relIdList relIdList
     * @param stageType stageType
     * @return rows
     */
    List<ApplicationActionLogDO> selectStatusInfoByRelIdList(@Param("relIdList") List<Long> relIdList, @Param("stageType") Integer stageType);

}
