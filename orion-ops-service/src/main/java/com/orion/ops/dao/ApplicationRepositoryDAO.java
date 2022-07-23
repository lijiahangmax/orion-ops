package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationRepositoryDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用版本仓库 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-11-26
 */
public interface ApplicationRepositoryDAO extends BaseMapper<ApplicationRepositoryDO> {

    /**
     * 通过id查询名称
     *
     * @param idList idList
     * @return rows
     */
    List<ApplicationRepositoryDO> selectNameByIdList(@Param("idList") List<Long> idList);

    /**
     * 通过名称查询id
     *
     * @param nameList nameList
     * @return rows
     */
    List<ApplicationRepositoryDO> selectIdByNameList(@Param("nameList") List<String> nameList);

}
