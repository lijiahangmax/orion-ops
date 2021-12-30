package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 发布单机器表 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
public interface ApplicationReleaseMachineDAO extends BaseMapper<ApplicationReleaseMachineDO> {

    /**
     * 查询发布机器状态
     *
     * @param releaseId releaseId
     * @return rows
     */
    List<ApplicationReleaseMachineDO> selectStatusByReleaseId(@Param("releaseId") Long releaseId);

    /**
     * 查询发布机器状态
     *
     * @param releaseIdList releaseIdList
     * @return rows
     */
    List<ApplicationReleaseMachineDO> selectStatusByReleaseIdList(@Param("releaseIdList") List<Long> releaseIdList);

    /**
     * 查询发布机器状态
     *
     * @param id id
     * @return row
     */
    ApplicationReleaseMachineDO selectStatusById(@Param("id") Long id);

    /**
     * 查询发布机器状态
     *
     * @param idList idList
     * @return rows
     */
    List<ApplicationReleaseMachineDO> selectStatusByIdList(@Param("idList") List<Long> idList);

}
