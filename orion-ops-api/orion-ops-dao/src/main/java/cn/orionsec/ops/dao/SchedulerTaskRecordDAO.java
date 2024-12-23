/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.dao;

import cn.orionsec.ops.entity.domain.SchedulerTaskRecordDO;
import cn.orionsec.ops.entity.dto.SchedulerTaskRecordStatisticsDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
    SchedulerTaskRecordStatisticsDTO getTaskRecordStatistics(@Param("taskId") Long taskId, @Param("rangeStartDate") Date rangeStartDate);

    /**
     * 获取调度任务机器统计
     *
     * @param taskId taskId
     * @return 统计信息
     */
    List<SchedulerTaskRecordStatisticsDTO> getTaskMachineRecordStatistics(@Param("taskId") Long taskId);

    /**
     * 获取调度任务时间线统计
     *
     * @param taskId         taskId
     * @param rangeStartDate 统计开始时间
     * @return 统计信息
     */
    List<SchedulerTaskRecordStatisticsDTO> getTaskRecordDateStatistics(@Param("taskId") Long taskId, @Param("rangeStartDate") Date rangeStartDate);

}
