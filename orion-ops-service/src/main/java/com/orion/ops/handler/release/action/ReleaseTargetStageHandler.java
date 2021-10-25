package com.orion.ops.handler.release.action;

import com.orion.constant.Letters;
import com.orion.lang.io.OutputAppender;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.dao.ReleaseMachineDAO;
import com.orion.ops.entity.domain.ReleaseMachineDO;
import com.orion.ops.handler.release.hint.ReleaseHint;
import com.orion.ops.handler.release.hint.ReleaseMachineHint;
import com.orion.ops.handler.tail.ITailHandler;
import com.orion.ops.handler.tail.TailSessionHolder;
import com.orion.spring.SpringHolder;
import com.orion.utils.Threads;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * 目标机器执行链
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/24 12:30
 */
@Slf4j
public class ReleaseTargetStageHandler extends AbstractReleaseActionHandler implements Runnable {

    private ReleaseMachineHint machine;

    private List<IReleaseActionHandler> actions;

    public ReleaseTargetStageHandler(ReleaseHint hint, ReleaseMachineHint machine, List<IReleaseActionHandler> actions) {
        super(hint, null);
        this.machine = machine;
        this.actions = actions;
    }

    @Override
    public void run() {
        this.handle();
    }

    @Override
    public void handle() {
        this.init();
        Exception e = null;
        try {
            // 执行操作
            this.handleAction();
        } catch (Exception ex) {
            e = ex;
        }
        // 完成回调
        this.processFinished(e);
        Streams.close(this);
    }

    @Override
    protected void handleAction() throws Exception {
        try {
            // 处理步骤
            for (IReleaseActionHandler action : actions) {
                action.handle();
            }
        } catch (Exception e) {
            // 异常跳过
            this.success = false;
            for (IReleaseActionHandler action : actions) {
                action.skip();
            }
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 初始化
     */
    private void init() {
        this.setLoggerAppender();
        this.handled = true;
        this.startTime = new Date();
        // 更新状态
        this.updateMachine(machine.getId(), ActionStatus.RUNNABLE, startTime, null);
        // 打印日志
        StringBuilder sb = new StringBuilder()
                .append("# 开始执行上线单宿主机操作").append(Letters.LF)
                .append("@ssh: ").append(machine.getUsername()).append("@")
                .append(machine.getHost()).append(":")
                .append(machine.getPort()).append(Letters.LF);
        sb.append("开始时间: ").append(Dates.format(startTime, Dates.YMD_HMS)).append(Letters.LF);
        this.appendLog(sb.toString());
    }

    /**
     * 处理完成回调
     *
     * @param e e
     */
    private void processFinished(Exception e) {
        // 关闭日志handler
        this.closeTailHandler();
        // 记录日志
        this.endTime = new Date();
        String interval = Dates.interval(endTime, startTime, "d", "h", "m", "s");
        StringBuilder sb = new StringBuilder(Const.LF)
                .append("# 远程机器操作执行操作").append(e == null ? "成功" : "失败")
                .append(", 结束时间: ").append(Dates.format(endTime, Dates.YMD_HMS))
                .append("; used ").append(interval);
        sb.append(" (").append(endTime.getTime() - startTime.getTime()).append(" ms)");
        this.appendLog(sb.toString());
        // 异常日志
        if (e != null) {
            this.success = false;
            log.error("远程机器操作执行操作-处理操作 异常: {} {}", e.getClass().getName(), e.getMessage());
            this.appendLog(e);
        }
        // 修改状态
        if (e == null) {
            this.updateMachine(machine.getId(), ActionStatus.FINISH, null, endTime);
        } else {
            this.updateMachine(machine.getId(), ActionStatus.EXCEPTION, null, endTime);
            this.onException(e);
        }
    }

    @Override
    public void onException(Exception e) {
        super.handlerException(e);
    }

    @Override
    protected void setLoggerAppender() {
        // 打开日志流
        OutputStream out = Files1.openOutputStreamFastSafe(machine.getLogPath());
        machine.setLogOutputStream(out);
        this.appender = OutputAppender.create(out);
    }

    /**
     * 修改 MachineStatus
     *
     * @param id        id
     * @param status    status
     * @param startTime startTime
     * @param endTime   endTime
     */
    private void updateMachine(Long id, ActionStatus status, Date startTime, Date endTime) {
        ReleaseMachineDO update = new ReleaseMachineDO();
        update.setId(id);
        update.setRunStatus(status.getStatus());
        update.setStartTime(startTime);
        update.setEndTime(endTime);
        SpringHolder.getBean(ReleaseMachineDAO.class).updateById(update);
    }

    @Override
    public void close() {
        super.close();
        Streams.close(machine.getLogOutputStream());
        actions.forEach(Streams::close);
    }

    /**
     * 关闭日志 tail handler
     */
    private void closeTailHandler() {
        // 关闭宿主机日志
        Threads.start(() -> {
            Threads.sleep(Const.MS_S_3);
            SpringHolder.getBean(TailSessionHolder.class)
                    .getSession(Const.HOST_MACHINE_ID, machine.getLogPath())
                    .forEach(ITailHandler::close);
        });
    }

}
