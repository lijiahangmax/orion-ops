/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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

import cn.orionsec.ops.entity.domain.ApplicationPipelineTaskDO;
import cn.orionsec.ops.entity.dto.ApplicationPipelineTaskStatisticsDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
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

    /**
     * 获取流水线任务统计
     *
     * @param pipelineId     pipelineId
     * @param rangeStartDate rangeStartDate
     * @return 统计信息
     */
    ApplicationPipelineTaskStatisticsDTO getPipelineTaskStatistics(@Param("pipelineId") Long pipelineId, @Param("rangeStartDate") Date rangeStartDate);

    /**
     * 获取流水线任务时间线统计
     *
     * @param pipelineId     pipelineId
     * @param rangeStartDate rangeStartDate
     * @return 时间线统计信息
     */
    List<ApplicationPipelineTaskStatisticsDTO> getPipelineTaskDateStatistics(@Param("pipelineId") Long pipelineId, @Param("rangeStartDate") Date rangeStartDate);

}
