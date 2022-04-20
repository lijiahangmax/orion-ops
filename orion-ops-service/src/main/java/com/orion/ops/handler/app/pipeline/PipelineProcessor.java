package com.orion.ops.handler.app.pipeline;

import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.app.PipelineDetailStatus;
import com.orion.ops.consts.app.PipelineStatus;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.message.MessageType;
import com.orion.ops.dao.ApplicationPipelineRecordDAO;
import com.orion.ops.entity.domain.ApplicationPipelineDetailRecordDO;
import com.orion.ops.entity.domain.ApplicationPipelineRecordDO;
import com.orion.ops.handler.app.pipeline.stage.IStageHandler;
import com.orion.ops.service.api.ApplicationPipelineDetailRecordService;
import com.orion.ops.service.api.WebSideMessageService;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.Threads;
import com.orion.utils.collect.Maps;
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

    private static ApplicationPipelineRecordDAO applicationPipelineRecordDAO = SpringHolder.getBean(ApplicationPipelineRecordDAO.class);

    private static ApplicationPipelineDetailRecordService applicationPipelineDetailRecordService = SpringHolder.getBean(ApplicationPipelineDetailRecordService.class);

    private static PipelineSessionHolder pipelineSessionHolder = SpringHolder.getBean(PipelineSessionHolder.class);

    private static WebSideMessageService webSideMessageService = SpringHolder.getBean(WebSideMessageService.class);

    @Getter
    private Long recordId;

    private ApplicationPipelineRecordDO record;

    private Map<Long, IStageHandler> stageHandlers;

    private volatile IStageHandler currentHandler;

    private volatile boolean terminated;

    public PipelineProcessor(Long recordId) {
        this.recordId = recordId;
        this.stageHandlers = Maps.newLinkedMap();
    }

    @Override
    public void exec() {
        log.info("已提交应用流水线任务 id: {}", recordId);
        Threads.start(this, SchedulerPools.PIPELINE_SCHEDULER);
    }

    @Override
    public void run() {
        log.info("开始执行应用流水线 id: {}", recordId);
        Exception ex = null;
        try {
            // 获取流水线数据
            this.getPipelineData();
            // 检查状态
            if (record != null && !PipelineStatus.WAIT_RUNNABLE.getStatus().equals(record.getExecStatus())
                    && !PipelineStatus.WAIT_SCHEDULE.getStatus().equals(record.getExecStatus())) {
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
            pipelineSessionHolder.removeSession(recordId);
        }
    }

    /**
     * 获取流水线数据
     */
    private void getPipelineData() {
        // 获取主表
        this.record = applicationPipelineRecordDAO.selectById(recordId);
        if (record == null) {
            return;
        }
        PipelineStatus status = PipelineStatus.of(record.getExecStatus());
        if (!PipelineStatus.WAIT_RUNNABLE.equals(status)
                && !PipelineStatus.WAIT_SCHEDULE.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 获取详情
        List<ApplicationPipelineDetailRecordDO> details = applicationPipelineDetailRecordService.selectRecordDetails(recordId);
        for (ApplicationPipelineDetailRecordDO detail : details) {
            stageHandlers.put(detail.getId(), IStageHandler.with(record, detail));
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
        log.info("应用流水线执行停止 id: {}", recordId);
        this.updateStatus(PipelineStatus.TERMINATED);
    }

    /**
     * 完成回调
     */
    private void completeCallback() {
        log.info("应用流水线执行完成 id: {}", recordId);
        this.updateStatus(PipelineStatus.FINISH);
        // 发送站内信
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.ID, record.getId());
        params.put(EventKeys.NAME, record.getPipelineName());
        params.put(EventKeys.TITLE, record.getExecTitle());
        webSideMessageService.addMessage(MessageType.PIPELINE_EXEC_SUCCESS, record.getExecUserId(), record.getExecUserName(), params);

    }

    /**
     * 异常回调
     *
     * @param ex ex
     */
    private void exceptionCallback(Exception ex) {
        log.error("应用流水线执行失败 id: {}", recordId, ex);
        this.updateStatus(PipelineStatus.FAILURE);
        // 发送站内信
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.ID, record.getId());
        params.put(EventKeys.NAME, record.getPipelineName());
        params.put(EventKeys.TITLE, record.getExecTitle());
        webSideMessageService.addMessage(MessageType.PIPELINE_EXEC_FAILURE, record.getExecUserId(), record.getExecUserName(), params);
    }

    /**
     * 更新状态
     *
     * @param status status
     */
    private void updateStatus(PipelineStatus status) {
        Date now = new Date();
        ApplicationPipelineRecordDO update = new ApplicationPipelineRecordDO();
        update.setId(recordId);
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
        applicationPipelineRecordDAO.updateById(update);
    }

    @Override
    public void terminate() {
        log.info("应用流水线执行停止 id: {}", recordId);
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
