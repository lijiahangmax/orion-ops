package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.SchedulerTaskRecordDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 调度任务执行日志 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
public interface SchedulerTaskRecordDAO extends BaseMapper<SchedulerTaskRecordDO> {

    /**
     * 查询任务状态
     *
     * @param idList idList
     * @return rows
     */
    List<SchedulerTaskRecordDO> selectTaskStatusByIdList(@Param("idList") List<Long> idList);

}
