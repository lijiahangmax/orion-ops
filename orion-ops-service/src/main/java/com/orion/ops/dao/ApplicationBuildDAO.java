package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import org.apache.ibatis.annotations.Param;

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

}
