package com.orion.ops.handler.app.release;

import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.app.ReleaseStatus;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.message.MessageType;
import com.orion.ops.dao.ApplicationReleaseDAO;
import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import com.orion.ops.handler.app.machine.ReleaseMachineProcessor;
import com.orion.ops.service.api.ApplicationReleaseMachineService;
import com.orion.ops.service.api.WebSideMessageService;
import com.orion.spring.SpringHolder;
import com.orion.utils.Threads;
import com.orion.utils.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 发布处理器基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/28 17:18
 */
@Slf4j
public abstract class AbstractReleaseProcessor implements IReleaseProcessor {

    protected static ApplicationReleaseDAO applicationReleaseDAO = SpringHolder.getBean(ApplicationReleaseDAO.class);

    protected static ApplicationReleaseMachineService applicationReleaseMachineService = SpringHolder.getBean(ApplicationReleaseMachineService.class);

    protected static ReleaseSessionHolder releaseSessionHolder = SpringHolder.getBean(ReleaseSessionHolder.class);

    private static WebSideMessageService webSideMessageService = SpringHolder.getBean(WebSideMessageService.class);

    @Getter
    private Long releaseId;

    protected ApplicationReleaseDO release;

    protected Map<Long, ReleaseMachineProcessor> machineProcessors;

    protected volatile boolean terminated;

    public AbstractReleaseProcessor(Long releaseId) {
        this.releaseId = releaseId;
        this.machineProcessors = Maps.newLinkedMap();
    }

    @Override
    public void exec() {
        log.info("已提交应用发布执行任务 id: {}", releaseId);
        Threads.start(this, SchedulerPools.RELEASE_MAIN_SCHEDULER);
    }

    @Override
    public void run() {
        log.info("应用发布任务执行开始 id: {}", releaseId);
        // 执行
        Exception ex = null;
        try {
            // 查询数据
            this.getReleaseData();
            // 检查状态
            if (release != null && !ReleaseStatus.WAIT_RUNNABLE.getStatus().equals(release.getReleaseStatus())
                    && !ReleaseStatus.WAIT_SCHEDULE.getStatus().equals(release.getReleaseStatus())) {
                return;
            }
            // 修改状态
            this.updateStatus(ReleaseStatus.RUNNABLE);
            // 添加会话
            releaseSessionHolder.addSession(this);
            // 执行
            this.handler();
        } catch (Exception e) {
            log.error("应用发布任务执行初始化失败 id: {}, {}", releaseId, e);
            ex = e;
        }
        // 回调
        try {
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
            // 释放资源
            this.close();
        }
    }

    /**
     * 处理器
     *
     * @throws Exception Exception
     */
    protected abstract void handler() throws Exception;

    @Override
    public void terminatedAll() {
        this.terminated = true;
    }

    @Override
    public void terminatedMachine(Long releaseMachineId) {
        ReleaseMachineProcessor processor = machineProcessors.get(releaseMachineId);
        if (processor != null) {
            processor.terminated();
        }
    }

    @Override
    public void skippedMachine(Long releaseMachineId) {
        ReleaseMachineProcessor processor = machineProcessors.get(releaseMachineId);
        if (processor != null) {
            processor.skipped();
        }
    }

    /**
     * 获取发布数据
     */
    protected void getReleaseData() {
        // 查询发布信息主表
        this.release = applicationReleaseDAO.selectById(releaseId);
        if (release == null) {
            return;
        }
        if (!ReleaseStatus.WAIT_RUNNABLE.getStatus().equals(release.getReleaseStatus())
                && !ReleaseStatus.WAIT_SCHEDULE.getStatus().equals(release.getReleaseStatus())) {
            return;
        }
        // 查询发布机器
        List<ApplicationReleaseMachineDO> machines = applicationReleaseMachineService.getReleaseMachines(releaseId);
        for (ApplicationReleaseMachineDO machine : machines) {
            machineProcessors.put(machine.getId(), new ReleaseMachineProcessor(release, machine));
        }
    }

    /**
     * 停止回调
     */
    protected void terminatedCallback() {
        log.info("应用发布任务执行执行停止 id: {}", releaseId);
        this.updateStatus(ReleaseStatus.TERMINATED);
    }

    /**
     * 完成回调
     */
    protected void completeCallback() {
        log.info("应用发布任务执行执行完成 id: {}", releaseId);
        this.updateStatus(ReleaseStatus.FINISH);
        // 发送站内信
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.ID, release.getId());
        params.put(EventKeys.TITLE, release.getReleaseTitle());
        webSideMessageService.addMessage(MessageType.RELEASE_SUCCESS, release.getReleaseUserId(), release.getReleaseUserName(), params);
    }

    /**
     * 异常回调
     *
     * @param ex ex
     */
    protected void exceptionCallback(Exception ex) {
        log.error("应用发布任务执行执行失败 id: {}", releaseId, ex);
        this.updateStatus(ReleaseStatus.FAILURE);
        // 发送站内信
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.ID, release.getId());
        params.put(EventKeys.TITLE, release.getReleaseTitle());
        webSideMessageService.addMessage(MessageType.RELEASE_FAILURE, release.getReleaseUserId(), release.getReleaseUserName(), params);
    }

    /**
     * 更新状态
     *
     * @param status status
     */
    protected void updateStatus(ReleaseStatus status) {
        Date now = new Date();
        ApplicationReleaseDO update = new ApplicationReleaseDO();
        update.setId(releaseId);
        update.setReleaseStatus(status.getStatus());
        update.setUpdateTime(now);
        switch (status) {
            case RUNNABLE:
                update.setReleaseStartTime(now);
                break;
            case FINISH:
            case TERMINATED:
            case FAILURE:
                update.setReleaseEndTime(now);
                break;
            default:
                break;
        }
        applicationReleaseDAO.updateById(update);
    }

    @Override
    public void close() {
        releaseSessionHolder.removeSession(releaseId);
    }

}
