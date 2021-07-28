package com.orion.ops.handler.release.processor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.able.SafeCloseable;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.app.ReleaseStatus;
import com.orion.ops.dao.ApplicationMachineDAO;
import com.orion.ops.dao.ReleaseBillDAO;
import com.orion.ops.entity.domain.ApplicationMachineDO;
import com.orion.ops.entity.domain.ReleaseBillDO;
import com.orion.ops.handler.release.action.IReleaseActionHandler;
import com.orion.ops.handler.release.action.ReleaseTargetChainActionHandler;
import com.orion.ops.handler.release.hint.ReleaseHint;
import com.orion.ops.handler.release.hint.ReleaseMachineHint;
import com.orion.spring.SpringHolder;
import com.orion.utils.Strings;
import com.orion.utils.Threads;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 上线单 宿主机处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/15 17:18
 */
@Slf4j
public class ReleaseProcessor implements Runnable, SafeCloseable {

    /**
     * 配置项
     */
    private ReleaseHint hint;

    /**
     * 宿主机操作
     */
    private List<IReleaseActionHandler> hostActions;

    /**
     * 目标机器操作
     */
    private List<ReleaseTargetChainActionHandler> targetChains;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    public ReleaseProcessor(ReleaseHint hint, List<IReleaseActionHandler> hostActions, List<ReleaseTargetChainActionHandler> targetChains) {
        this.hint = hint;
        this.hostActions = hostActions;
        this.targetChains = targetChains;
    }

    @SneakyThrows
    @Override
    public void run() {
        try {
            this.init();
            Exception e = null;
            // 执行宿主机命令
            try {
                for (IReleaseActionHandler hostAction : hostActions) {
                    hostAction.handle();
                    hostAction.close();
                }
            } catch (Exception ex) {
                // skip
                e = ex;
                for (IReleaseActionHandler hostAction : hostActions) {
                    hostAction.skip();
                }
                e.printStackTrace();
            }
            ReleaseStatus status;
            // 执行目标机器
            if (e == null) {
                if (!targetChains.isEmpty()) {
                    // 修改目标机器版本
                    this.updateAppMachineReleaseId();
                    // 阻塞执行目标机器操作
                    Threads.blockRun(targetChains, SchedulerPools.RELEASE_TARGET_CHAIN_SCHEDULER);
                    boolean allMatchineSuccess = targetChains.stream()
                            .map(ReleaseTargetChainActionHandler::isSuccess)
                            .allMatch(Boolean::booleanValue);
                    if (allMatchineSuccess) {
                        status = ReleaseStatus.FINISH;
                    } else {
                        status = ReleaseStatus.EXCEPTION;
                    }
                } else {
                    status = ReleaseStatus.FINISH;
                }
            } else {
                status = ReleaseStatus.EXCEPTION;
            }
            // 结束
            this.processFinished(e, status);
        } finally {
            this.close();
        }
    }

    /**
     * 初始化
     */
    private void init() {
        // 记录时间
        this.startTime = new Date();
        // 更新时间
        this.updateRelease(hint.getReleaseId(), null, startTime, null);
        // 打开日志流
        hint.setHostLogOutputStream(Files1.openOutputStreamFastSafe(hint.getHostLogPath()));
        // 打印日志
        StringBuilder sb = new StringBuilder()
                .append("# 开始执行上线单宿主机操作\n")
                .append("id: ").append(hint.getReleaseId()).append(Const.LF)
                .append("执行人: ").append(hint.getReleaseUserId()).append(Const.SPACE_4)
                .append(hint.getReleaseUserName()).append(Const.LF)
                .append("应用: ").append(hint.getAppName()).append(Const.SPACE_4)
                .append(hint.getProfileName()).append(Const.LF)
                .append("标题: ").append(hint.getTitle()).append(Const.LF);
        if (!Strings.isBlank(hint.getDescription())) {
            sb.append("描述: ").append(hint.getDescription()).append(Const.LF);
        }
        sb.append("开始时间: ").append(Dates.format(startTime, Dates.YMDHMS)).append(Const.LF);
        this.appendLog(sb.toString());
    }

    /**
     * 处理完成回调
     *
     * @param e      e
     * @param status status
     */
    private void processFinished(Exception e, ReleaseStatus status) {
        // 记录日志
        this.endTime = new Date();
        String interval = Dates.interval(endTime, startTime, "d", "h", "m", "s");
        StringBuilder sb = new StringBuilder()
                .append("# 上线单执行").append(e == null ? "成功" : "失败")
                .append(", 结束时间: ").append(Dates.format(endTime, Dates.YMDHMS))
                .append("; used ").append(interval);
        sb.append(" (").append(endTime.getTime() - startTime.getTime()).append(" ms)");
        this.appendLog(sb.toString());
        // 异常日志
        if (e != null) {
            log.error("上线单执行运行异常: {} {}", e.getClass().getName(), e.getMessage());
            e.printStackTrace();
        }
        // 修改状态
        this.updateRelease(hint.getReleaseId(), status, null, endTime);
    }

    /**
     * 输出日志到宿主机日志
     *
     * @param message message
     * @param args    args
     */
    private void appendLog(String message, Object... args) {
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
    private void appendLog(Exception e) {
        try {
            OutputStream hostLogOutputStream = hint.getHostLogOutputStream();
            e.printStackTrace(new PrintStream(hostLogOutputStream));
            hostLogOutputStream.flush();
        } catch (Exception ex) {
            log.error("上线单处理记录日志失败 异常: {}", ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        // 关闭日志
        Streams.close(hint.getHostLogOutputStream());
        // 释放宿主机操作资源
        hostActions.forEach(Streams::close);
        // 释放目标机器操作资源
        targetChains.forEach(Streams::close);
        // 释放连接
        hint.getSessionHolder().forEach((k, v) -> Streams.close(v));
    }

    /**
     * 更新 ReleaseBill
     *
     * @param id        id
     * @param status    status
     * @param startTime startTime
     * @param endTime   endTime
     */
    private void updateRelease(Long id, ReleaseStatus status, Date startTime, Date endTime) {
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
    private void updateAppMachineReleaseId() {
        List<Long> machineIds = hint.getMachines().stream()
                .map(ReleaseMachineHint::getMachineId)
                .collect(Collectors.toList());
        // 更新
        ApplicationMachineDO update = new ApplicationMachineDO();
        update.setReleaseId(hint.getReleaseId());
        LambdaQueryWrapper<ApplicationMachineDO> wrapper = new LambdaQueryWrapper<ApplicationMachineDO>()
                .eq(ApplicationMachineDO::getAppId, hint.getAppId())
                .eq(ApplicationMachineDO::getProfileId, hint.getProfileId())
                .in(ApplicationMachineDO::getMachineId, machineIds);
        SpringHolder.getBean(ApplicationMachineDAO.class).update(update, wrapper);
    }

}
