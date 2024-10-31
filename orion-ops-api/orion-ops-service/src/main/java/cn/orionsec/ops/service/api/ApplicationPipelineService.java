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

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.ops.entity.request.app.ApplicationPipelineRequest;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineVO;

import java.util.List;

/**
 * 应用流水线服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:16
 */
public interface ApplicationPipelineService {

    /**
     * 添加流水线
     *
     * @param request request
     * @return request
     */
    Long addPipeline(ApplicationPipelineRequest request);

    /**
     * 更新流水线
     *
     * @param request request
     * @return effect
     */
    Integer updatePipeline(ApplicationPipelineRequest request);

    /**
     * 流水线列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<ApplicationPipelineVO> listPipeline(ApplicationPipelineRequest request);

    /**
     * 获取流水线详情
     *
     * @param id id
     * @return row
     */
    ApplicationPipelineVO getPipeline(Long id);

    /**
     * 删除流水线
     *
     * @param idList idList
     * @return effect
     */
    Integer deletePipeline(List<Long> idList);

    /**
     * 通过 profileId 删除
     *
     * @param profileId profileId
     * @return effect
     */
    Integer deleteByProfileId(Long profileId);

}
