package com.orion.ops.handler.release.action;

import com.orion.exception.LogException;
import com.orion.exception.argument.HttpWrapperException;
import com.orion.exception.argument.InvalidArgumentException;
import com.orion.lang.io.OutputAppender;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.handler.release.hint.ReleaseActionHint;
import com.orion.ops.handler.release.hint.ReleaseHint;
import com.orion.support.Attempt;
import com.orion.utils.Strings;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintStream;
import java.util.Date;

/**
 * 宿主机执行器基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/17 12:07
 */
@Slf4j
public abstract class AbstractReleaseActionHandler implements IReleaseActionHandler {

    /**
     * hint
     */
    protected ReleaseHint hint;

    /**
     * 操作
     */
    protected ReleaseActionHint action;

    protected Date startTime;

    protected Date endTime;

    protected boolean handled;

    protected OutputAppender appender;

    protected boolean success;

    public AbstractReleaseActionHandler(ReleaseHint hint, ReleaseActionHint action) {
        this.hint = hint;
        this.action = action;
        this.success = true;
    }

    @Override
    public void handle() throws Exception {
        this.setLoggerAppender();
        this.handled = true;
        this.startTime = new Date();
        this.appendLog("# 开始执行上线单步骤操作-{} {}", action.getName(), Dates.format(startTime));
        this.updateAction(action.getId(), ActionStatus.RUNNABLE, startTime, null);
        Exception e = null;
        try {
            // 执行操作
            this.handleAction();
        } catch (Exception ex) {
            e = ex;
        }
        // 完成回调
        this.endTime = new Date();
        if (e == null) {
            this.appendLog("# 上线单步骤操作执行完成-{} {}\n", action.getName(), Dates.format(startTime));
            this.updateAction(action.getId(), ActionStatus.FINISH, null, endTime);
        } else {
            this.success = false;
            log.error("上线单处理宿主机操作-处理操作 异常: {}", e.getMessage());
            this.appendLog("# 上线单步骤操作执行失败-{} {}\n", action.getName(), Dates.format(startTime));
            this.updateAction(action.getId(), ActionStatus.EXCEPTION, null, endTime);
            this.onException(e);
        }
        Streams.close(this);
    }

    @Override
    public void onException(Exception e) throws Exception {
        this.handlerException(e);
        throw e;
    }

    /**
     * 处理异常
     *
     * @param e e
     */
    protected void handlerException(Exception e) {
        if (e instanceof HttpWrapperException) {
            HttpWrapper<?> wrapper = ((HttpWrapperException) e).getWrapper();
            if (wrapper != null) {
                this.appendLog(wrapper.getMsg());
            } else {
                this.appendLog(e.getMessage());
            }
        } else if (e instanceof InvalidArgumentException) {
            this.appendLog(e.getMessage());
        } else if (e instanceof LogException) {
            this.appendLog(e.getMessage());
            if (((LogException) e).hasCause()) {
                this.appendLog(e);
            }
        } else {
            this.appendLog(e);
        }
    }

    @Override
    public void skip() {
        if (handled) {
            return;
        }
        this.appendLog("----- 操作步骤跳过-{}", action.getName());
        this.updateAction(action.getId(), ActionStatus.SKIPPED, null, null);
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    /**
     * 处理命令
     *
     * @throws Exception Exception
     */
    protected abstract void handleAction() throws Exception;

    /**
     * 设置日志信息
     */
    protected void setLoggerAppender() {
        this.appender = OutputAppender.create(Files1.openOutputStreamFastSafe(action.getLogPath()));
    }

    /**
     * 输出日志到appender
     *
     * @param message message
     * @param args    args
     */
    protected void appendLog(String message, Object... args) {
        try {
            byte[] bytes = Strings.bytes(Strings.format(message, args) + "\n");
            appender.write(bytes);
        } catch (Exception e) {
            log.error("上线单处理操作步骤-记录日志 异常: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * 输出日志到appender
     *
     * @param e e
     */
    protected void appendLog(Exception e) {
        try {
            appender.handle(Attempt.rethrows(o -> {
                e.printStackTrace(new PrintStream(o));
            }));
        } catch (Exception ex) {
            log.error("上线单处理宿主机命操作-记录日志 异常: {}", ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        Streams.close(appender);
    }

}
