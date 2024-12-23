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
package cn.orionsec.ops.service.api;

import cn.orionsec.ops.entity.domain.ApplicationPipelineDetailDO;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineDetailVO;

import java.util.List;

/**
 * 应用流水线详情服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:53
 */
public interface ApplicationPipelineDetailService {

    /**
     * 通过 pipelineId 查询
     *
     * @param pipelineId pipelineId
     * @return rows
     */
    List<ApplicationPipelineDetailVO> getByPipelineId(Long pipelineId);

    /**
     * 通过 pipelineId 查询
     *
     * @param pipelineId pipelineId
     * @return rows
     */
    List<ApplicationPipelineDetailDO> selectByPipelineId(Long pipelineId);

    /**
     * 通过 pipelineId 查询
     *
     * @param pipelineIdList pipelineIdList
     * @return rows
     */
    List<ApplicationPipelineDetailDO> selectByPipelineIdList(List<Long> pipelineIdList);

    /**
     * 通过 pipelineId 删除
     *
     * @param pipelineId pipelineId
     * @return effect
     */
    Integer deleteByPipelineId(Long pipelineId);

    /**
     * 通过 pipelineId 删除
     *
     * @param pipelineIdList pipelineIdList
     * @return effect
     */
    Integer deleteByPipelineIdList(List<Long> pipelineIdList);

    /**
     * 通过 profileId 删除
     *
     * @param profileId profileId
     * @return effect
     */
    Integer deleteByProfileId(Long profileId);

    /**
     * 通过 appId 删除
     *
     * @param appId appId
     * @return effect
     */
    Integer deleteByAppId(Long appId);

}
