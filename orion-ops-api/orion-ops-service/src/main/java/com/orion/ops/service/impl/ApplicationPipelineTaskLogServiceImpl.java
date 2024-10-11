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
package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.dao.ApplicationPipelineTaskLogDAO;
import com.orion.ops.entity.domain.ApplicationPipelineTaskLogDO;
import com.orion.ops.entity.vo.app.ApplicationPipelineTaskLogVO;
import com.orion.ops.service.api.ApplicationPipelineTaskLogService;
import com.orion.ops.utils.DataQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用流水线任务日志服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/24 20:53
 */
@Service("applicationPipelineTaskLogService")
public class ApplicationPipelineTaskLogServiceImpl implements ApplicationPipelineTaskLogService {

    @Resource
    private ApplicationPipelineTaskLogDAO applicationPipelineTaskLogDAO;

    @Override
    public List<ApplicationPipelineTaskLogVO> getLogList(Long taskId) {
        LambdaQueryWrapper<ApplicationPipelineTaskLogDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskLogDO>()
                .eq(ApplicationPipelineTaskLogDO::getTaskId, taskId);
        return DataQuery.of(applicationPipelineTaskLogDAO)
                .wrapper(wrapper)
                .list(ApplicationPipelineTaskLogVO.class);
    }

    @Override
    public Integer deleteByTaskIdList(List<Long> taskIdList) {
        Wrapper<ApplicationPipelineTaskLogDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskLogDO>()
                .in(ApplicationPipelineTaskLogDO::getTaskId, taskIdList);
        return applicationPipelineTaskLogDAO.delete(wrapper);
    }

}
