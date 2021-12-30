package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationReleaseDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 发布单 Mapper 接口
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

}
