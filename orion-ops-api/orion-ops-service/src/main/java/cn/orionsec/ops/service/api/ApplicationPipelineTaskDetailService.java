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
package cn.orionsec.ops.service.api;

import cn.orionsec.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineTaskDetailVO;

import java.util.List;

/**
 * 应用流水线任务详情服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:55
 */
public interface ApplicationPipelineTaskDetailService {

    /**
     * 获取流水线详情明细
     *
     * @param taskId taskId
     * @return rows
     */
    List<ApplicationPipelineTaskDetailVO> getTaskDetails(Long taskId);

    /**
     * 获取流水线详情明细
     *
     * @param taskId taskId
     * @return rows
     */
    List<ApplicationPipelineTaskDetailDO> selectTaskDetails(Long taskId);

    /**
     * 获取流水线详情明细
     *
     * @param taskIdList taskIdList
     * @return rows
     */
    List<ApplicationPipelineTaskDetailDO> selectTaskDetails(List<Long> taskIdList);

    /**
     * 通过 taskId 删除详情明细
     *
     * @param taskId taskId
     * @return effect
     */
    Integer deleteByTaskId(Long taskId);

    /**
     * 通过 taskId 删除详情明细
     *
     * @param taskIdList taskIdList
     * @return effect
     */
    Integer deleteByTaskIdList(List<Long> taskIdList);

}
