package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationInfoDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用表 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-07-02
 */
public interface ApplicationInfoDAO extends BaseMapper<ApplicationInfoDO> {

    /**
     * 获取 vcs 数量
     *
     * @param vcsId vcsId
     * @return count
     */
    Integer selectVcsCount(@Param("vcsId") Long vcsId);

    /**
     * 清空 vcs
     *
     * @param vcsId vcsId
     * @return effect
     */
    Integer cleanVcsCount(@Param("vcsId") Long vcsId);

    /**
     * 查询名称
     *
     * @param id id
     * @return name
     */
    String selectNameById(@Param("id") Long id);

    /**
     * 查询名称
     *
     * @param idList idList
     * @return name
     */
    List<ApplicationInfoDO> selectNameByIdList(@Param("idList") List<Long> idList);

}
