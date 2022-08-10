package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.ops.entity.dto.ApplicationReleaseStatisticsDTO;
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
