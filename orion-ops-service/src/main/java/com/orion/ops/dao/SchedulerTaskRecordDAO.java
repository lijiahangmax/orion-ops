package com.orion.ops.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.ops.entity.domain.SchedulerTaskRecordDO;
import com.orion.ops.entity.dto.SchedulerTaskRecordStatisticsDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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

    /**
     * 获取调度任务统计
     *
     * @param taskId         taskId
     * @param rangeStartDate 统计开始时间
     * @return 统计信息
     */
    SchedulerTaskRecordStatisticsDTO getTaskRecordStatistic(@Param("taskId") Long taskId, @Param("rangeStartDate") Date rangeStartDate);

    /**
     * 获取调度任务机器统计
     *
     * @param taskId taskId
     * @return 统计信息
     */
    List<SchedulerTaskRecordStatisticsDTO> getTaskMachineRecordStatistic(@Param("taskId") Long taskId);

    /**
     * 获取调度任务时间线统计
     *
     * @param taskId         taskId
     * @param rangeStartDate 统计开始时间
     * @return 统计信息
     */
    List<SchedulerTaskRecordStatisticsDTO> getTaskRecordDateStatistic(@Param("taskId") Long taskId, @Param("rangeStartDate") Date rangeStartDate);

}
