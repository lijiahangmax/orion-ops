package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationVcsDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用版本控制 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-11-26
 */
public interface ApplicationVcsDAO extends BaseMapper<ApplicationVcsDO> {

    /**
     * 通过id查询名称
     *
     * @param idList idList
     * @return rows
     */
    List<ApplicationVcsDO> selectNameByIdList(@Param("idList") List<Long> idList);

    /**
     * 通过名称查询id
     *
     * @param nameList nameList
     * @return rows
     */
    List<ApplicationVcsDO> selectIdByNameList(@Param("nameList") List<String> nameList);

}
