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
package cn.orionsec.ops.handler.app.pipeline;

import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.kit.lang.utils.Threads;
import cn.orionsec.kit.lang.utils.collect.Maps;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.SchedulerPools;
import cn.orionsec.ops.constant.app.PipelineDetailStatus;
import cn.orionsec.ops.constant.app.PipelineStatus;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.constant.message.MessageType;
import cn.orionsec.ops.dao.ApplicationPipelineTaskDAO;
import cn.orionsec.ops.entity.domain.ApplicationPipelineTaskDO;
import cn.orionsec.ops.entity.domain.ApplicationPipelineTaskDetailDO;
import cn.orionsec.ops.handler.app.pipeline.stage.IStageHandler;
import cn.orionsec.ops.service.api.ApplicationPipelineTaskDetailService;
import cn.orionsec.ops.service.api.WebSideMessageService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 流水线执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/15 14:40
 */
@Slf4j
public class PipelineProcessor implements IPipelineProcessor {

    private static final ApplicationPipelineTaskDAO applicationPipelineTaskDAO = SpringHolder.getBean(ApplicationPipelineTaskDAO.class);

    private static final ApplicationPipelineTaskDetailService applicationPipelineTaskDetailService = SpringHolder.getBean(ApplicationPipelineTaskDetailService.class);

    private static final PipelineSessionHolder pipelineSessionHolder = SpringHolder.getBean(PipelineSessionHolder.class);

    private static final WebSideMessageService webSideMessageService = SpringHolder.getBean(WebSideMessageService.class);

    @Getter
    private final Long taskId;

    private ApplicationPipelineTaskDO task;

    private final Map<Long, IStageHandler> stageHandlers;

    private volatile IStageHandler currentHandler;

    private volatile boolean terminated;

    public PipelineProcessor(Long taskId) {
        this.taskId = taskId;
        this.stageHandlers = Maps.newLinkedMap();
    }

    @Override
    public void exec() {
        log.info("已提交应用流水线任务 id: {}", taskId);
        Threads.start(this, SchedulerPools.PIPELINE_SCHEDULER);
    }

    @Override
    public void run() {
        log.info("开始执行应用流水线 id: {}", taskId);
        Exception ex = null;
        try {
            // 获取流水线数据
            this.getPipelineData();
            // 检查状态
            if (task != null && !PipelineStatus.WAIT_RUNNABLE.getStatus().equals(task.getExecStatus())
                    && !PipelineStatus.WAIT_SCHEDULE.getStatus().equals(task.getExecStatus())) {
                return;
            }
            // 修改状态
            this.updateStatus(PipelineStatus.RUNNABLE);
            // 添加会话
            pipelineSessionHolder.addSession(this);
            // 执行
            this.handlerPipeline();
        } catch (Exception e) {
            ex = e;
        }
        try {
            // 回调
            if (terminated) {
                // 停止回调
                this.terminatedCallback();
            } else if (ex == null) {
                // 成功回调
                this.completeCallback();
            } else {
                // 异常回调
                this.exceptionCallback(ex);
            }
        } finally {
            pipelineSessionHolder.removeSession(taskId);
        }
    }

    /**
     * 获取流水线数据
     */
    private void getPipelineData() {
        // 获取主表
        this.task = applicationPipelineTaskDAO.selectById(taskId);
        if (task == null) {
            return;
        }
        PipelineStatus status = PipelineStatus.of(task.getExecStatus());
        if (!PipelineStatus.WAIT_RUNNABLE.equals(status)
                && !PipelineStatus.WAIT_SCHEDULE.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 获取详情
        List<ApplicationPipelineTaskDetailDO> details = applicationPipelineTaskDetailService.selectTaskDetails(taskId);
        for (ApplicationPipelineTaskDetailDO detail : details) {
            stageHandlers.put(detail.getId(), IStageHandler.with(task, detail));
        }
    }

    /**
     * 执行流水线操作
     *
     * @throws Exception exception
     */
    private void handlerPipeline() throws Exception {
        Exception ex = null;
        Collection<IStageHandler> handlers = stageHandlers.values();
        for (IStageHandler stageHandler : handlers) {
            this.currentHandler = stageHandler;
            // 停止或异常则跳过
            if (terminated || ex != null) {
                stageHandler.skip();
                continue;
            }
            // 执行
            try {
                stageHandler.exec();
            } catch (Exception e) {
                ex = e;
            }
        }
        this.currentHandler = null;
        // 异常返回
        if (ex != null) {
            throw ex;
        }
        // 全部停止
        final boolean allTerminated = handlers.stream()
                .map(IStageHandler::getStatus)
                .filter(s -> !PipelineDetailStatus.SKIPPED.equals(s))
                .allMatch(PipelineDetailStatus.TERMINATED::equals);
        if (allTerminated) {
            this.terminated = true;
        }
    }

    /**
     * 停止回调
     */
    private void terminatedCallback() {
        log.info("应用流水线执行停止 id: {}", taskId);
        this.updateStatus(PipelineStatus.TERMINATED);
    }

    /**
     * 完成回调
     */
    private void completeCallback() {
        log.info("应用流水线执行完成 id: {}", taskId);
        this.updateStatus(PipelineStatus.FINISH);
        // 发送站内信
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.ID, task.getId());
        params.put(EventKeys.NAME, task.getPipelineName());
        params.put(EventKeys.TITLE, task.getExecTitle());
        webSideMessageService.addMessage(MessageType.PIPELINE_EXEC_SUCCESS, task.getId(), task.getExecUserId(), task.getExecUserName(), params);

    }

    /**
     * 异常回调
     *
     * @param ex ex
     */
    private void exceptionCallback(Exception ex) {
        log.error("应用流水线执行失败 id: {}", taskId, ex);
        this.updateStatus(PipelineStatus.FAILURE);
        // 发送站内信
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.ID, task.getId());
        params.put(EventKeys.NAME, task.getPipelineName());
        params.put(EventKeys.TITLE, task.getExecTitle());
        webSideMessageService.addMessage(MessageType.PIPELINE_EXEC_FAILURE, task.getId(), task.getExecUserId(), task.getExecUserName(), params);
    }

    /**
     * 更新状态
     *
     * @param status status
     */
    private void updateStatus(PipelineStatus status) {
        Date now = new Date();
        ApplicationPipelineTaskDO update = new ApplicationPipelineTaskDO();
        update.setId(taskId);
        update.setExecStatus(status.getStatus());
        update.setUpdateTime(now);
        switch (status) {
            case RUNNABLE:
                update.setExecStartTime(now);
                break;
            case FINISH:
            case TERMINATED:
            case FAILURE:
                update.setExecEndTime(now);
                break;
            default:
                break;
        }
        applicationPipelineTaskDAO.updateById(update);
    }

    @Override
    public void terminate() {
        log.info("应用流水线执行停止 id: {}", taskId);
        this.terminated = true;
        if (currentHandler != null) {
            currentHandler.terminate();
        }
    }

    @Override
    public void terminateDetail(Long id) {
        IStageHandler stageHandler = stageHandlers.get(id);
        if (stageHandler != null) {
            stageHandler.terminate();
        }
    }

    @Override
    public void skipDetail(Long id) {
        IStageHandler stageHandler = stageHandlers.get(id);
        if (stageHandler != null) {
            stageHandler.skip();
        }
    }

}
