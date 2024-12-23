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

import cn.orionsec.ops.entity.domain.SchedulerTaskMachineRecordDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

    /**
     * 通过 recordIdList 删除明细
     *
     * @param recordIdList recordIdList
     * @return effect
     */
    Integer deleteByRecordIdList(@Param("recordIdList") List<Long> recordIdList);

}
