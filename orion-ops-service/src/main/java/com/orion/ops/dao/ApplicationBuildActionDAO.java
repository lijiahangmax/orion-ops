package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationBuildActionDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用构建操作 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-11-26
 */
public interface ApplicationBuildActionDAO extends BaseMapper<ApplicationBuildActionDO> {

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
     * @return status
     */
    ApplicationBuildActionDO selectStatusInfoById(@Param("id") Long id);

    /**
     * 通过 buildId 查询 id
     *
     * @param buildId buildId
     * @return id
     */
    List<Long> selectActionIdByBuildId(@Param("buildId") Long buildId);

    /**
     * 查询上一次构建分支
     *
     * @param appId     appId
     * @param profileId profileId
     * @param vcsId     vcsId
     * @return branch
     */
    String selectLastBuildBranch(@Param("appId") Long appId, @Param("profileId") Long profileId, @Param("vcsId") Long vcsId);

}
