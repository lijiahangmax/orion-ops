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

import cn.orionsec.ops.entity.domain.ApplicationBuildDO;
import cn.orionsec.ops.entity.dto.ApplicationBuildStatisticsDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 应用构建 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-11-26
 */
public interface ApplicationBuildDAO extends BaseMapper<ApplicationBuildDO> {

    /**
     * 通过 id 查询状态
     *
     * @param id id
     * @return status
     */
    Integer selectStatusById(@Param("id") Long id);

    /**
     * 通过 id 查询状态信息
     *
     * @param id id
     * @return row
     */
    ApplicationBuildDO selectStatusInfoById(@Param("id") Long id);

    /**
     * 通过 id 查询状态信息
     *
     * @param idList idList
     * @return rows
     */
    List<ApplicationBuildDO> selectStatusInfoByIdList(@Param("idList") List<Long> idList);

    /**
     * 查询上一次构建分支
     *
     * @param appId     appId
     * @param profileId profileId
     * @param repoId    repoId
     * @return branch
     */
    String selectLastBuildBranch(@Param("appId") Long appId, @Param("profileId") Long profileId, @Param("repoId") Long repoId);

    /**
     * 查询构建序列
     *
     * @param id id
     * @return seq
     */
    Integer selectBuildSeq(@Param("id") Long id);

    /**
     * 查询构建发布列表
     *
     * @param appId     appId
     * @param profileId profileId
     * @param limit     limit
     * @return rows
     */
    List<ApplicationBuildDO> selectBuildReleaseList(@Param("appId") Long appId, @Param("profileId") Long profileId, @Param("limit") Integer limit);

    /**
     * 查询构建发布列表
     *
     * @param appIdList appIdList
     * @param profileId profileId
     * @return rows
     */
    List<ApplicationBuildDO> selectBuildReleaseGroupList(@Param("appIdList") List<Long> appIdList, @Param("profileId") Long profileId);

    /**
     * 获取构建统计
     *
     * @param appId          appId
     * @param profileId      profileId
     * @param rangeStartDate rangeStartDate
     * @return 统计信息
     */
    ApplicationBuildStatisticsDTO getBuildStatistics(@Param("appId") Long appId, @Param("profileId") Long profileId, @Param("rangeStartDate") Date rangeStartDate);

    /**
     * 获取构建时间线统计
     *
     * @param appId          appId
     * @param profileId      profileId
     * @param rangeStartDate rangeStartDate
     * @return 时间线统计信息
     */
    List<ApplicationBuildStatisticsDTO> getBuildDateStatistics(@Param("appId") Long appId, @Param("profileId") Long profileId, @Param("rangeStartDate") Date rangeStartDate);

}
