package com.orion.ops.handler.app.machine;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.dao.ApplicationMachineDAO;
import com.orion.ops.dao.ApplicationReleaseMachineDAO;
import com.orion.ops.entity.domain.ApplicationActionLogDO;
import com.orion.ops.entity.domain.ApplicationMachineDO;
import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import com.orion.ops.handler.app.action.IActionHandler;
import com.orion.ops.handler.app.action.MachineActionStore;
import com.orion.ops.service.api.ApplicationActionLogService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.remote.channel.SessionStore;
import com.orion.spring.SpringHolder;
import com.orion.utils.Valid;
import com.orion.utils.time.Dates;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * 发布机器执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/12 16:15
 */
@Slf4j
public class ReleaseMachineProcessor extends AbstractMachineProcessor {

    protected static ApplicationReleaseMachineDAO applicationReleaseMachineDAO = SpringHolder.getBean(ApplicationReleaseMachineDAO.class);

    protected static ApplicationMachineDAO applicationMachineDAO = SpringHolder.getBean(ApplicationMachineDAO.class);

    protected static ApplicationActionLogService applicationActionLogService = SpringHolder.getBean(ApplicationActionLogService.class);

    protected static MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    private ApplicationReleaseDO release;

    private ApplicationReleaseMachineDO machine;

    private MachineActionStore store;

    @Getter
    private volatile ActionStatus status;

    public ReleaseMachineProcessor(ApplicationReleaseDO release, ApplicationReleaseMachineDO machine) {
        super(machine.getId());
        this.release = release;
        this.machine = machine;
        this.status = ActionStatus.WAIT;
        this.store = new MachineActionStore();
        store.setRelId(machine.getId());
        store.setMachineId(machine.getMachineId());
        store.setBundlePath(release.getBundlePath());
        store.setTransferPath(release.getTransferPath());
    }

    @Override
    public void run() {
        log.info("应用发布任务执行开始 releaseId: {}", id);
        // 初始化数据
        this.initData();
        // 执行
        super.run();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 查询action
        List<ApplicationActionLogDO> actions = applicationActionLogService.selectActionByRelId(id, StageType.RELEASE);
        Valid.notEmpty(actions, MessageConst.UNKNOWN_DATA);
        actions.forEach(s -> store.getActions().put(s.getId(), s));
        log.info("应用发布器-获取数据-action releaseId: {}, actions: {}", id, JSON.toJSONString(actions));
        // 创建handler
        this.handlerList = IActionHandler.createHandler(actions, store);
    }

    @Override
    public void skipped() {
        this.updateStatus(MachineProcessorStatus.SKIPPED);
    }

    @Override
    protected boolean checkCanRunnable() {
        return ActionStatus.WAIT.getStatus().equals(machine.getRunStatus());
    }

    @Override
    protected void openLogger() {
        super.openLogger();
        store.setSuperLogStream(this.logStream);
    }

    @Override
    protected String getLogPath() {
        return machine.getLogPath();
    }

    @Override
    protected void handlerFinishCallback(Exception ex, boolean isMainError) {
        super.handlerFinishCallback(ex, isMainError);
        // 更新状态
        if (ex == null && !terminated) {
            // 更新应用机器发布版本
            this.updateAppMachineVersion();
        }
    }

    @Override
    protected void openMachineSession() {
        // 打开session
        SessionStore sessionStore = machineInfoService.openSessionStore(machine.getMachineId());
        store.setSessionStore(sessionStore);
    }

    @Override
    protected void updateStatus(MachineProcessorStatus processorStatus) {
        this.status = ActionStatus.valueOf(processorStatus.name());
        Date now = new Date();
        ApplicationReleaseMachineDO update = new ApplicationReleaseMachineDO();
        update.setId(id);
        update.setRunStatus(status.getStatus());
        update.setUpdateTime(now);
        switch (processorStatus) {
            case RUNNABLE:
                this.startTime = now;
                update.setStartTime(now);
                break;
            case FINISH:
            case TERMINATED:
            case FAILURE:
                this.endTime = now;
                update.setEndTime(now);
                break;
            default:
                break;
        }
        applicationReleaseMachineDAO.updateById(update);
    }

    @Override
    protected void appendStartedLog() {
        StringBuilder log = new StringBuilder()
                .append("# 开始执行机器操作 ")
                .append(machine.getMachineName())
                .append(Const.TAB);
        log.append(machine.getMachineHost())
                .append(Const.TAB)
                .append(Dates.format(startTime))
                .append(Const.LF);
        this.appendLog(log.toString());
    }

    /**
     * 更新应用机器版本
     */
    private void updateAppMachineVersion() {
        ApplicationMachineDO update = new ApplicationMachineDO();
        update.setAppId(release.getAppId());
        update.setProfileId(release.getProfileId());
        update.setMachineId(machine.getMachineId());
        update.setReleaseId(release.getId());
        update.setBuildId(release.getBuildId());
        update.setBuildSeq(release.getBuildSeq());
        applicationMachineDAO.updateAppVersion(update);
    }

}
