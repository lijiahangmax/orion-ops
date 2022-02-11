package com.orion.ops.handler.app.release.machine;

import com.orion.exception.LogException;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.ApplicationMachineDAO;
import com.orion.ops.dao.ApplicationReleaseMachineDAO;
import com.orion.ops.entity.domain.ApplicationMachineDO;
import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import com.orion.ops.handler.app.store.MachineStore;
import com.orion.ops.handler.app.store.ReleaseStore;
import com.orion.ops.handler.app.release.handler.IReleaseHandler;
import com.orion.ops.handler.tail.ITailHandler;
import com.orion.ops.handler.tail.TailSessionHolder;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.remote.channel.SessionStore;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.Threads;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Date;

/**
 * 机器处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/29 8:55
 */
@Slf4j
public class MachineProcessor implements IMachineProcessor {

    protected static ApplicationReleaseMachineDAO applicationReleaseMachineDAO = SpringHolder.getBean(ApplicationReleaseMachineDAO.class);

    protected static ApplicationMachineDAO applicationMachineDAO = SpringHolder.getBean(ApplicationMachineDAO.class);

    protected static MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    protected static TailSessionHolder tailSessionHolder = SpringHolder.getBean(TailSessionHolder.class);

    private ReleaseStore releaseStore;

    private MachineStore machineStore;

    private Long id;

    /**
     * 发布机器
     */
    private ApplicationReleaseMachineDO machine;

    @Getter
    private volatile ActionStatus status;

    /**
     * 是否已终止
     */
    private volatile boolean terminated;

    public MachineProcessor(ReleaseStore releaseStore, MachineStore machineStore) {
        this.releaseStore = releaseStore;
        this.machineStore = machineStore;
        this.id = machineStore.getId();
        this.machine = machineStore.getMachine();
        this.status = ActionStatus.WAIT;
    }

    @Override
    public void run() {
        log.info("开始执行应用发布机器流程 id: {}", id);
        Exception ex = null;
        boolean isMainError = false;
        try {
            // 检查状态
            ApplicationReleaseMachineDO machineStatus = applicationReleaseMachineDAO.selectStatusById(id);
            if (!ActionStatus.WAIT.getStatus().equals(machineStatus.getRunStatus())) {
                this.status = ActionStatus.of(machineStatus.getRunStatus());
                return;
            }
            // 更新状态
            this.updateStatus(ActionStatus.RUNNABLE);
            // 初始化日志
            this.initLogger();
            // 初始化机器
            SessionStore sessionStore = machineInfoService.openSessionStore(machineStore.getMachineId());
            machineStore.setSessionStore(sessionStore);
            // 执行操作
            for (IReleaseHandler handler : machineStore.getHandler()) {
                if (ex == null && !terminated) {
                    try {
                        handler.exec();
                    } catch (Exception e) {
                        if (!terminated) {
                            ex = e;
                        }
                    }
                } else {
                    handler.skipped();
                }
            }
        } catch (Exception e) {
            ex = e;
            isMainError = true;
            log.error("应用发布机器流程执行失败 id: {}, e: {}", id, e);
        }
        // 更新状态
        if (terminated) {
            ex = null;
        } else if (ex == null) {
            this.updateStatus(ActionStatus.FINISH);
            // 更新应用机器发布版本
            this.updateAppMachineVersion();
        } else {
            this.updateStatus(ActionStatus.FAILURE);
        }
        this.appendFinishedLog(ex, isMainError);
        // 关闭
        Streams.close(this);
        if (ex != null) {
            throw Exceptions.runtime(ex.getMessage(), ex);
        }
    }

    /**
     * 初始化日志
     */
    private void initLogger() {
        String logPath = Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), machine.getLogPath());
        log.info("应用发布机器流程-打开日志 id: {}, path: {}", id, logPath);
        File logFile = new File(logPath);
        Files1.touch(logFile);
        machineStore.setLogStream(Files1.openOutputStreamFastSafe(logFile));
        machineStore.setLogPath(logFile.getAbsolutePath());
        // 拼接开始日志
        this.appendStartedLog();
    }

    /**
     * 拼接开始日志
     */
    private void appendStartedLog() {
        StringBuilder log = new StringBuilder()
                .append("# 开始执行机器操作 ").append(machine.getMachineName()).append(Const.TAB)
                .append(machine.getMachineHost()).append(Const.TAB)
                .append(Dates.format(machine.getStartTime())).append(Const.LF);
        this.appendLog(log.toString());
    }

    /**
     * 拼接完成日志
     *
     * @param ex          ex
     * @param isMainError isMainError
     */
    private void appendFinishedLog(Exception ex, boolean isMainError) {
        StringBuilder log = new StringBuilder();
        if (ex != null) {
            // 有异常
            log.append("# 执行机器操作失败 ").append(Dates.format(machine.getEndTime()))
                    .append("; used: ").append(machine.getEndTime().getTime() - machine.getStartTime().getTime())
                    .append("ms\n");
        } else {
            if (terminated) {
                log.append("# 执行机器操作手动停止 结束时间: ").append(Dates.format(machine.getEndTime())).append(Const.LF);
            } else {
                log.append("# 执行机器操作完成 ").append(Dates.format(machine.getEndTime()))
                        .append("; used: ").append(machine.getEndTime().getTime() - machine.getStartTime().getTime())
                        .append("ms\n");
            }
        }
        // 拼接日志
        this.appendLog(log.toString());
        // 拼接异常
        if (ex != null && isMainError) {
            if (ex instanceof LogException) {
                this.appendLog(ex.getMessage() + Const.LF);
            } else {
                this.appendLog(Exceptions.getStackTraceAsString(ex) + Const.LF);
            }
        }
    }

    /**
     * 拼接日志
     *
     * @param log log
     */
    @SneakyThrows
    private void appendLog(String log) {
        machineStore.getLogStream().write(log.getBytes());
    }

    /**
     * 更新状态
     *
     * @param status status
     */
    private void updateStatus(ActionStatus status) {
        this.status = status;
        Date now = new Date();
        ApplicationReleaseMachineDO update = new ApplicationReleaseMachineDO();
        update.setId(id);
        update.setRunStatus(status.getStatus());
        update.setUpdateTime(now);
        switch (status) {
            case RUNNABLE:
                machine.setStartTime(now);
                update.setStartTime(now);
                break;
            case FINISH:
            case TERMINATED:
            case FAILURE:
                machine.setEndTime(now);
                update.setEndTime(now);
                break;
            default:
                break;
        }
        applicationReleaseMachineDAO.updateById(update);
    }

    /**
     * 更新应用机器版本
     */
    private void updateAppMachineVersion() {
        ApplicationMachineDO update = new ApplicationMachineDO();
        ApplicationReleaseDO record = releaseStore.getRecord();
        update.setAppId(record.getAppId());
        update.setProfileId(record.getProfileId());
        update.setMachineId(machine.getMachineId());
        update.setReleaseId(record.getId());
        update.setBuildId(record.getBuildId());
        update.setBuildSeq(record.getBuildSeq());
        applicationMachineDAO.updateAppVersion(update);
    }

    @Override
    public void skipped() {
        this.updateStatus(ActionStatus.SKIPPED);
    }

    @Override
    public void terminated() {
        // 设置状态为已停止
        this.terminated = true;
        // 更新状态
        this.updateStatus(ActionStatus.TERMINATED);
        // 结束正在执行的action
        for (IReleaseHandler handler : machineStore.getHandler()) {
            if (ActionStatus.RUNNABLE.equals(handler.getStatus())) {
                handler.terminated();
            }
        }
    }

    @Override
    public void close() {
        Streams.close(machineStore.getLogStream());
        Streams.close(machineStore.getSessionStore());
        machineStore.getHandler().forEach(Streams::close);
        // 异步关闭正在tail的日志
        Threads.start(() -> {
            try {
                Threads.sleep(Const.MS_S_10);
                tailSessionHolder.getSession(Const.HOST_MACHINE_ID, machineStore.getLogPath()).forEach(ITailHandler::close);
            } catch (Exception e) {
                log.error("machineProcessor-关闭tail失败 {} {}", machineStore, e);
                e.printStackTrace();
            }
        });
    }

}
