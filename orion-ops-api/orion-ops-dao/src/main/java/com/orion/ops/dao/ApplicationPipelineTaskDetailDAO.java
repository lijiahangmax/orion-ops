package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用流水线任务详情 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-04-07
 */
public interface ApplicationPipelineTaskDetailDAO extends BaseMapper<ApplicationPipelineTaskDetailDO> {

    /**
     * 查询任务详情状态
     *
     * @param taskId taskId
     * @return rows
     */
    List<ApplicationPipelineTaskDetailDO> selectStatusByTaskId(@Param("taskId") Long taskId);

    /**
     * 查询任务详情状态
     *
     * @param idList idList
     * @return rows
     */
    List<ApplicationPipelineTaskDetailDO> selectStatusByIdList(@Param("idList") List<Long> idList);

}
