package com.orion.ops.handler.release.action;

import com.orion.exception.LogException;
import com.orion.exception.argument.HttpWrapperException;
import com.orion.exception.argument.InvalidArgumentException;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.handler.release.hint.ReleaseActionHint;
import com.orion.ops.handler.release.hint.ReleaseHint;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
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
public abstract class AbstractReleaseHostActionHandler implements IReleaseActionHandler {

    /**
     * hint
     */
    protected ReleaseHint hint;

    /**
     * 操作
     */
    protected ReleaseActionHint action;

    /**
     * 开始时间
     */
    protected Date startDate;

    /**
     * 结束时间
     */
    protected Date endDate;

    protected boolean handled;

    public AbstractReleaseHostActionHandler(ReleaseHint hint, ReleaseActionHint action) {
        this.hint = hint;
        this.action = action;
        this.setLogger();
    }

    @Override
    public void handle() {
        this.handled = true;
        this.startDate = new Date();
        this.printLog(">>>>> 开始执行宿主机操作-{} {}", action.getName(), Dates.format(startDate));
        this.updateActionStatus(action.getId(), ActionStatus.RUNNABLE, startDate, null);
        Exception e = null;
        try {
            // 执行操作
            this.handleAction();
        } catch (Exception ex) {
            e = ex;
        }
        // 完成回调
        this.endDate = new Date();
        if (e == null) {
            this.printLog("<<<<< 宿主机操作执行完成-{} {}", action.getName(), Dates.format(startDate));
            this.updateActionStatus(action.getId(), ActionStatus.RUNNABLE, null, endDate);
        } else {
            log.error("上线单处理宿主机操作-处理操作 异常: {}", e.getMessage());
            this.printLog("<<<<< 宿主机操作执行失败-{} {}", action.getName(), Dates.format(startDate));
            this.updateActionStatus(action.getId(), ActionStatus.EXCEPTION, null, endDate);
            this.onException(e);
        }
    }

    @Override
    public void onException(Exception e) {
        if (e instanceof HttpWrapperException) {
            HttpWrapper<?> wrapper = ((HttpWrapperException) e).getWrapper();
            if (wrapper != null) {
                this.printLog(wrapper.getMsg());
            } else {
                this.printLog(e.getMessage());
            }
        } else if (e instanceof InvalidArgumentException) {
            this.printLog(e.getMessage());
        } else if (e instanceof LogException) {
            if (((LogException) e).hasCause()) {
                this.printLog(e);
            } else {
                this.printLog(e.getMessage());
            }
        } else {
            this.printLog(e);
        }
        throw Exceptions.runtime(e);
    }

    @Override
    public boolean isHandled() {
        return this.handled;
    }

    /**
     * 处理命令
     *
     * @throws Exception Exception
     */
    protected abstract void handleAction() throws Exception;

    /**
     * 设置日志信息
     * 如果需要单独记录action日志则重写
     */
    protected void setLogger() {
    }

    /**
     * 输出日志到宿主机日志
     *
     * @param message message
     * @param args    args
     */
    protected void printLog(String message, Object... args) {
        try {
            byte[] bytes = Strings.bytes(Strings.format(message, args) + "\n");
            OutputStream hostLogOutputStream = hint.getHostLogOutputStream();
            if (hostLogOutputStream != null) {
                hostLogOutputStream.write(bytes);
                hostLogOutputStream.flush();
            }
            OutputStream logOutputStream = action.getLogOutputStream();
            if (logOutputStream != null) {
                logOutputStream.write(bytes);
                logOutputStream.flush();
            }
        } catch (Exception e) {
            log.error("上线单处理宿主机命操作-记录日志 异常: {}", e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * 输出日志到宿主机日志
     *
     * @param e e
     */
    protected void printLog(Exception e) {
        try {
            OutputStream hostLogOutputStream = hint.getHostLogOutputStream();
            if (hostLogOutputStream != null) {
                e.printStackTrace(new PrintStream(hostLogOutputStream));
                hostLogOutputStream.flush();
            }
            OutputStream logOutputStream = action.getLogOutputStream();
            if (logOutputStream != null) {
                e.printStackTrace(new PrintStream(logOutputStream));
                logOutputStream.flush();
            }
        } catch (Exception ex) {
            log.error("上线单处理宿主机命操作-记录日志 异常: {}", ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    @Override
    public void close() {
        Streams.close(action.getLogOutputStream());
    }

}
