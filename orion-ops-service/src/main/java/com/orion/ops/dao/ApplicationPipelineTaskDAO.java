package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用流水线任务 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-04-07
 */
public interface ApplicationPipelineTaskDAO extends BaseMapper<ApplicationPipelineTaskDO> {

    /**
     * 设置定时执行时间为空
     *
     * @param id id
     * @return effect
     */
    Integer setTimedExecTimeNull(@Param("id") Long id);

    /**
     * 查询任务状态
     *
     * @param id id
     * @return row
     */
    ApplicationPipelineTaskDO selectStatusById(@Param("id") Long id);

    /**
     * 查询任务状态
     *
     * @param idList idList
     * @return rows
     */
    List<ApplicationPipelineTaskDO> selectStatusByIdList(@Param("idList") List<Long> idList);

}
