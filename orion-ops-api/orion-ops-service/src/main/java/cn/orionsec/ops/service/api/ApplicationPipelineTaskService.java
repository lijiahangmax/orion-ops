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
import cn.orionsec.ops.entity.request.app.ApplicationPipelineTaskRequest;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineTaskListVO;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineTaskStatusVO;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineTaskVO;

import java.util.Date;
import java.util.List;

/**
 * 应用流水线任务服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/7 8:55
 */
public interface ApplicationPipelineTaskService {

    /**
     * 获取列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<ApplicationPipelineTaskListVO> getPipelineTaskList(ApplicationPipelineTaskRequest request);

    /**
     * 获取详情
     *
     * @param id id
     * @return task
     */
    ApplicationPipelineTaskVO getPipelineTaskDetail(Long id);

    /**
     * 提交流水线执行
     *
     * @param request request
     * @return id
     */
    Long submitPipelineTask(ApplicationPipelineTaskRequest request);

    /**
     * 审核流水线
     *
     * @param request request
     * @return effect
     */
    Integer auditPipelineTask(ApplicationPipelineTaskRequest request);

    /**
     * 复制流水线
     *
     * @param id id
     * @return effect
     */
    Long copyPipeline(Long id);

    /**
     * 执行流水线
     *
     * @param id       id
     * @param isSystem 是否为系统执行
     */
    void execPipeline(Long id, boolean isSystem);

    /**
     * 删除流水线
     *
     * @param idList idList
     * @return effect
     */
    Integer deletePipeline(List<Long> idList);

    /**
     * 设置定时执行流水线
     *
     * @param id            id
     * @param timedExecDate timedExecDate
     */
    void setPipelineTimedExec(Long id, Date timedExecDate);

    /**
     * 取消定时执行流水线
     *
     * @param id id
     */
    void cancelPipelineTimedExec(Long id);

    /**
     * 停止执行流水线
     *
     * @param id id
     */
    void terminateExec(Long id);

    /**
     * 停止执行流水线
     *
     * @param id       id
     * @param detailId detailId
     */
    void terminateExecDetail(Long id, Long detailId);

    /**
     * 停止执行流水线
     *
     * @param id       id
     * @param detailId detailId
     */
    void skipExecDetail(Long id, Long detailId);

    /**
     * 任务状态
     *
     * @param id id
     * @return status
     */
    ApplicationPipelineTaskStatusVO getTaskStatus(Long id);

    /**
     * 任务状态列表
     *
     * @param idList       idList
     * @param detailIdList detailIdList
     * @return status
     */
    List<ApplicationPipelineTaskStatusVO> getTaskStatusList(List<Long> idList, List<Long> detailIdList);

}
