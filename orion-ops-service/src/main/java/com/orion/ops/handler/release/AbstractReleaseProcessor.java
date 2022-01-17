package com.orion.ops.handler.release;

import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.app.ReleaseStatus;
import com.orion.ops.dao.ApplicationReleaseDAO;
import com.orion.ops.entity.domain.ApplicationReleaseActionDO;
import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import com.orion.ops.handler.release.handler.IReleaseHandler;
import com.orion.ops.handler.release.machine.IMachineProcessor;
import com.orion.ops.handler.release.machine.MachineStore;
import com.orion.ops.service.api.ApplicationReleaseActionService;
import com.orion.ops.service.api.ApplicationReleaseMachineService;
import com.orion.ops.utils.Valid;
import com.orion.spring.SpringHolder;
import com.orion.utils.Threads;
import com.orion.utils.io.Streams;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

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

    protected static ApplicationReleaseActionService applicationReleaseActionService = SpringHolder.getBean(ApplicationReleaseActionService.class);

    protected static ReleaseSessionHolder releaseSessionHolder = SpringHolder.getBean(ReleaseSessionHolder.class);

    @Getter
    private Long releaseId;

    protected ReleaseStore store;

    /**
     * 是否已结束
     */
    protected volatile boolean terminated;

    public AbstractReleaseProcessor(Long releaseId) {
        this.releaseId = releaseId;
        this.store = new ReleaseStore();
    }

    @Override
    public void exec() {
        log.info("已提交应用发布执行任务 id: {}", releaseId);
        Threads.start(this, SchedulerPools.RELEASE_MAIN_SCHEDULER);
    }

    @Override
    public void run() {
        log.info("应用发布任务执行开始 id: {}", releaseId);
        try {
            // 查询数据
            this.getReleaseData();
            // 检查状态
            if (!ReleaseStatus.WAIT_RUNNABLE.getStatus().equals(store.getRecord().getReleaseStatus())) {
                return;
            }
            this.updateStatus(ReleaseStatus.RUNNABLE);
            releaseSessionHolder.addSession(this);
        } catch (Exception e) {
            log.error("应用发布任务执行初始化失败 id: {}, {}", releaseId, e);
            this.updateStatus(ReleaseStatus.INITIAL_ERROR);
            return;
        }
        // 执行
        try {
            this.handler();
            if (terminated) {
                log.info("应用发布任务执行执行停止 id: {}", releaseId);
                this.updateStatus(ReleaseStatus.TERMINATED);
            } else {
                log.info("应用发布任务执行执行完成 id: {}", releaseId);
                this.updateStatus(ReleaseStatus.FINISH);
            }
        } catch (Exception e) {
            log.error("应用发布任务执行执行失败 id: {}, {}", releaseId, e);
            this.updateStatus(ReleaseStatus.FAILURE);
        } finally {
            releaseSessionHolder.removeSession(releaseId);
            Streams.close(this);
        }
    }

    /**
     * 处理器
     *
     * @throws Exception Exception
     */
    protected abstract void handler() throws Exception;

    @Override
    public void terminated() {
        this.terminated = true;
    }

    /**
     * 获取发布数据
     */
    protected void getReleaseData() {
        // 查询发布信息主表
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(releaseId);
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        store.setRecord(release);
        if (!ReleaseStatus.WAIT_RUNNABLE.getStatus().equals(release.getReleaseStatus())) {
            return;
        }
        // 查询发布机器
        List<ApplicationReleaseMachineDO> releaseMachines = applicationReleaseMachineService.getReleaseMachines(releaseId);
        for (ApplicationReleaseMachineDO releaseMachine : releaseMachines) {
            // 设置store
            MachineStore machineStore = new MachineStore();
            machineStore.setId(releaseMachine.getId());
            machineStore.setMachineId(releaseMachine.getMachineId());
            machineStore.setMachine(releaseMachine);
            // 设置处理器
            store.getMachineProcessors().put(releaseMachine.getId(), IMachineProcessor.with(store, machineStore));
            // 设置发布操作
            List<ApplicationReleaseActionDO> actions = applicationReleaseActionService.getReleaseActionByReleaseMachineId(releaseMachine.getId());
            actions.forEach(s -> machineStore.getActions().put(s.getId(), s));
            List<IReleaseHandler> handlers = IReleaseHandler.with(actions, store, machineStore);
            machineStore.setHandler(handlers);
            store.getMachines().put(releaseMachine.getId(), machineStore);
        }
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
            case INITIAL_ERROR:
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
        store.getMachineProcessors().values().forEach(Streams::close);
    }

}
