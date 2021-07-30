package com.orion.ops.handler.release.processor;

import com.orion.ops.consts.Const;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.app.ReleaseStatus;
import com.orion.ops.dao.ReleaseBillDAO;
import com.orion.ops.entity.domain.ReleaseBillDO;
import com.orion.ops.handler.release.action.IReleaseActionHandler;
import com.orion.ops.handler.release.action.ReleaseTargetStageHandler;
import com.orion.ops.handler.release.hint.ReleaseHint;
import com.orion.ops.handler.release.hint.ReleaseMachineHint;
import com.orion.ops.handler.tail.ITailHandler;
import com.orion.ops.handler.tail.TailSessionHolder;
import com.orion.ops.service.api.ApplicationMachineService;
import com.orion.spring.SpringHolder;
import com.orion.utils.Strings;
import com.orion.utils.Threads;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 发布处理器 基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/30 18:13
 */
@Slf4j
public abstract class AbstractReleaseProcessor implements IReleaseProcessor {

    @Getter
    protected Long releaseId;

    /**
     * 配置项
     */
    protected ReleaseHint hint;

    /**
     * 宿主机操作
     */
    protected List<IReleaseActionHandler> hostActions;

    /**
     * 目标机器操作
     */
    protected List<ReleaseTargetStageHandler> targetStages;

    /**
     * 开始时间
     */
    protected Date startTime;

    /**
     * 结束时间
     */
    protected Date endTime;

    protected Exception exception;

    public AbstractReleaseProcessor(ReleaseHint hint, List<IReleaseActionHandler> hostActions, List<ReleaseTargetStageHandler> targetStages) {
        this.hint = hint;
        this.hostActions = hostActions;
        this.targetStages = targetStages;
        this.releaseId = hint.getReleaseId();
    }

    @SneakyThrows
    @Override
    public void run() {
        try {
            // 初始化
            this.init();
            // 执行宿主机 action
            boolean success = this.runHostAction();
            // 执行目标机器命令
            if (success && !targetStages.isEmpty()) {
                // 修改目标机器版本
                this.updateAppMachineReleaseId();
                // 执行目标机器 stage
                success = this.runTargetStage();
            }
            // 结束
            if (success) {
                this.processFinished(ReleaseStatus.FINISH);
            } else {
                this.processFinished(ReleaseStatus.EXCEPTION);
            }
        } catch (Exception e) {
            // 拼接日志
            this.appendLog(e);
            // 结束
            this.processFinished(ReleaseStatus.EXCEPTION);
            throw e;
        } finally {
            // 释放资源
            this.close();
        }
    }

    /**
     * 执行宿主机 stages
     *
     * @return 是否成功
     * @throws Exception Exception
     */
    protected abstract boolean runHostAction() throws Exception;

    /**
     * 执行目标机器 stages
     *
     * @return 是否成功
     * @throws Exception Exception
     */
    protected boolean runTargetStage() throws Exception {
        // 阻塞执行目标机器操作
        Threads.blockRun(targetStages, SchedulerPools.RELEASE_TARGET_STAGE_SCHEDULER);
        return targetStages.stream().allMatch(ReleaseTargetStageHandler::isSuccess);
    }

    /**
     * 输出日志到宿主机日志
     *
     * @param message message
     * @param args    args
     */
    protected void appendLog(String message, Object... args) {
        try {
            OutputStream hostLogOutputStream = hint.getHostLogOutputStream();
            hostLogOutputStream.write(Strings.bytes(Strings.format(message, args) + "\n"));
            hostLogOutputStream.flush();
        } catch (Exception e) {
            log.error("上线单处理记录日志失败 异常: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * 输出日志到宿主机日志
     *
     * @param e e
     */
    protected void appendLog(Exception e) {
        try {
            OutputStream hostLogOutputStream = hint.getHostLogOutputStream();
            e.printStackTrace(new PrintStream(hostLogOutputStream));
            hostLogOutputStream.flush();
        } catch (Exception ex) {
            log.error("上线单处理记录日志失败 异常: {}", ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    /**
     * 初始化
     */
    protected void init() {
        // 记录时间
        this.startTime = new Date();
        // 更新时间
        this.updateRelease(releaseId, ReleaseStatus.RUNNABLE, startTime, null);
        // 打开日志流
        hint.setHostLogOutputStream(Files1.openOutputStreamFastSafe(hint.getHostLogPath()));
    }

    /**
     * 处理完成回调
     *
     * @param status status
     */
    protected void processFinished(ReleaseStatus status) {
        // 记录日志
        this.endTime = new Date();
        String interval = Dates.interval(endTime, startTime, "d", "h", "m", "s");
        StringBuilder sb = new StringBuilder()
                .append("# 上线单执行").append(exception == null ? "成功" : "失败")
                .append(", 结束时间: ").append(Dates.format(endTime, Dates.YMDHMS))
                .append("; used ").append(interval);
        sb.append(" (").append(endTime.getTime() - startTime.getTime()).append(" ms)");
        this.appendLog(sb.toString());
        // 异常日志
        if (exception != null) {
            log.error("上线单执行运行异常: {} {}", exception.getClass().getName(), exception.getMessage());
            exception.printStackTrace();
        }
        // 修改状态
        this.updateRelease(releaseId, status, null, endTime);
    }

    @Override
    public void close() {
        // 关闭日志
        Streams.close(hint.getHostLogOutputStream());
        // 释放宿主机操作资源
        hostActions.forEach(Streams::close);
        // 释放目标机器操作资源
        targetStages.forEach(Streams::close);
        // 释放连接
        hint.getSessionHolder().forEach((k, v) -> Streams.close(v));
        // 关闭tail
        this.closeAllTailHandler();
    }

    /**
     * 更新 ReleaseBill
     *
     * @param id        id
     * @param status    status
     * @param startTime startTime
     * @param endTime   endTime
     */
    protected void updateRelease(Long id, ReleaseStatus status, Date startTime, Date endTime) {
        ReleaseBillDO update = new ReleaseBillDO();
        update.setId(id);
        update.setReleaseStatus(status.getStatus());
        update.setReleaseStartTime(startTime);
        update.setReleaseEndTime(endTime);
        SpringHolder.getBean(ReleaseBillDAO.class).updateById(update);
    }

    /**
     * 修改 ApplicationMachine releaseId
     */
    protected void updateAppMachineReleaseId() {
        List<Long> machineIds = hint.getMachines().stream()
                .map(ReleaseMachineHint::getMachineId)
                .collect(Collectors.toList());
        // 更新
        SpringHolder.getBean(ApplicationMachineService.class)
                .updateAppMachineReleaseId(hint.getAppId(), hint.getProfileId(), releaseId, machineIds);
    }

    /**
     * 关闭所有 tail handler
     */
    protected void closeAllTailHandler() {
        TailSessionHolder tailSessionHolder = SpringHolder.getBean(TailSessionHolder.class);
        // 关闭宿主机日志
        tailSessionHolder.getSession(Const.HOST_MACHINE_ID, hint.getHostLogPath())
                .forEach(ITailHandler::close);
        // 关闭目标机器日志
        for (ReleaseMachineHint machine : hint.getMachines()) {
            tailSessionHolder.getSession(Const.HOST_MACHINE_ID, machine.getLogPath())
                    .forEach(ITailHandler::close);
        }
    }

}
