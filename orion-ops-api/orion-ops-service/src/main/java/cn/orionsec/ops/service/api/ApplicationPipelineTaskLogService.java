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

import cn.orionsec.ops.entity.vo.app.ApplicationPipelineTaskLogVO;

import java.util.List;

/**
 * 应用流水线任务日志服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/24 20:48
 */
public interface ApplicationPipelineTaskLogService {

    /**
     * 获取日志列表
     *
     * @param taskId 任务id
     * @return rows
     */
    List<ApplicationPipelineTaskLogVO> getLogList(Long taskId);

    /**
     * 通过 taskIdList 删除
     *
     * @param taskIdList taskIdList
     * @return effect
     */
    Integer deleteByTaskIdList(List<Long> taskIdList);

}
