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
package cn.orionsec.ops.dao;

import cn.orionsec.ops.entity.domain.ApplicationReleaseDO;
import cn.orionsec.ops.entity.dto.ApplicationReleaseStatisticsDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 发布任务 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
public interface ApplicationReleaseDAO extends BaseMapper<ApplicationReleaseDO> {

    /**
     * 查询状态
     *
     * @param id id
     * @return row
     */
    ApplicationReleaseDO selectStatusById(@Param("id") Long id);

    /**
     * 查询状态列表
     *
     * @param idList idList
     * @return rows
     */
    List<ApplicationReleaseDO> selectStatusByIdList(@Param("idList") List<Long> idList);

    /**
     * 设置定时时间为 null
     *
     * @param id id
     * @return effect
     */
    Integer setTimedReleaseTimeNull(@Param("id") Long id);

    /**
     * 获取发布统计
     *
     * @param appId          appId
     * @param profileId      profileId
     * @param rangeStartDate rangeStartDate
     * @return 统计信息
     */
    ApplicationReleaseStatisticsDTO getReleaseStatistics(@Param("appId") Long appId, @Param("profileId") Long profileId, @Param("rangeStartDate") Date rangeStartDate);

    /**
     * 获取发布时间线统计
     *
     * @param appId          appId
     * @param profileId      profileId
     * @param rangeStartDate rangeStartDate
     * @return 时间线统计信息
     */
    List<ApplicationReleaseStatisticsDTO> getReleaseDateStatistics(@Param("appId") Long appId, @Param("profileId") Long profileId, @Param("rangeStartDate") Date rangeStartDate);

}
