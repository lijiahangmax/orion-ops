package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.SchedulerTaskMachineRecordDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 调度任务执行明细机器详情 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-02-22
 */
public interface SchedulerTaskMachineRecordDAO extends BaseMapper<SchedulerTaskMachineRecordDO> {

    /**
     * 通过 recordId 查询状态
     *
     * @param recordId recordId
     * @return rows
     */
    List<SchedulerTaskMachineRecordDO> selectStatusByRecordId(@Param("recordId") Long recordId);

    /**
     * 通过 idList 查询状态
     *
     * @param idList idList
     * @return rows
     */
    List<SchedulerTaskMachineRecordDO> selectStatusByIdList(@Param("idList") List<Long> idList);

    /**
     * 通过 recordIdList 查询状态
     *
     * @param recordIdList recordIdList
     * @return rows
     */
    List<SchedulerTaskMachineRecordDO> selectStatusByRecordIdList(@Param("recordIdList") List<Long> recordIdList);

}
