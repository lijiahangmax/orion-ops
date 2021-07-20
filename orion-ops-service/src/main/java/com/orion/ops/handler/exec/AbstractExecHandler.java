package com.orion.ops.handler.exec;

import com.alibaba.fastjson.JSON;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.command.ExecStatus;
import com.orion.ops.dao.CommandExecDAO;
import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import com.orion.remote.channel.ssh.BaseRemoteExecutor;
import com.orion.remote.channel.ssh.CommandExecutor;
import com.orion.spring.SpringHolder;
import com.orion.utils.Threads;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Date;

/**
 * 命令执行器 基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/7 17:17
 */
@Slf4j
public abstract class AbstractExecHandler implements IExecHandler {

    protected static CommandExecDAO commandExecDAO = SpringHolder.getBean("commandExecDAO");

    protected static ExecSessionHolder execSessionHolder = SpringHolder.getBean("execSessionHolder");

    @Getter
    protected ExecHint hint;

    @Getter
    protected Long execId;

    @Getter
    protected CommandExecutor executor;

    protected MachineInfoDO machine;

    protected AbstractExecHandler(ExecHint hint) {
        this.hint = hint;
        this.valid();
    }

    @Override
    public Long submit(ExecHint hint) {
        log.info("execHandler-执行命令开始 machineId: {}, type: {}, startDate: {}", hint.getMachineId(), hint.getExecType(), Dates.current(Dates.YMDHMSS));
        this.machine = hint.getMachine();
        this.insertRecord();
        Threads.start(this, SchedulerPools.EXEC_SCHEDULER);
        return execId;
    }

    @Override
    public void run() {
        try {
            // 打开commandExecutor
            this.executor = hint.getSession().getCommandExecutor(hint.getCommand());
            execSessionHolder.addSession(execId, this);
            this.updateStatus(ExecStatus.RUNNABLE);
            this.openComputed();

            // 开始执行
            executor.inherit()
                    .sync()
                    .streamHandler(this::processStandardOutputStream)
                    .callback(this::callback)
                    .connect()
                    .exec();
        } catch (Exception e) {
            this.onException(e);
            throw e;
        } finally {
            this.close();
        }
    }

    /**
     * 命令打开完成
     */
    protected abstract void openComputed();

    /**
     * 处理命令输出
     *
     * @param in in
     */
    protected abstract void processStandardOutputStream(InputStream in);

    /**
     * 完成回调
     *
     * @param executor executor
     */
    protected void callback(BaseRemoteExecutor executor) {
        int exitCode = ((CommandExecutor) executor).getExitCode();
        hint.setExitCode(exitCode);
        log.info("execHandler-执行命令完成回调 execId: {} code: {}", execId, exitCode);
        CommandExecDO updateStatus = new CommandExecDO();
        updateStatus.setId(execId);
        updateStatus.setExitCode(exitCode);
        updateStatus.setEndDate(new Date());
        updateStatus.setExecStatus(ExecStatus.COMPLETE.getStatus());
        commandExecDAO.updateById(updateStatus);
    }

    /**
     * 发生异常时调用
     *
     * @param e e
     */
    protected void onException(Exception e) {
        log.error("execHandler-执行命令失败 execId: {} {}", execId, e);
        e.printStackTrace();
        this.updateStatus(ExecStatus.EXCEPTION);
    }

    /**
     * 参数合法校验
     */
    protected void valid() {
        Valid.notNull(hint.getExecType());
        Valid.notNull(hint.getMachineId());
        Valid.notBlank(hint.getCommand());
        Valid.notNull(hint.getSession());
    }

    /**
     * 插入执行到库
     */
    protected void insertRecord() {
        Date startDate = new Date();
        UserDTO user = Currents.getUser();
        CommandExecDO insert = new CommandExecDO();
        insert.setUserId(user.getId());
        insert.setUserName(user.getUsername());
        insert.setRelId(hint.getRelId());
        insert.setExecType(hint.getExecType().getType());
        insert.setExecCommand(hint.getCommand());
        insert.setDescription(hint.getDescription());
        insert.setMachineId(hint.getMachineId());
        insert.setExecStatus(ExecStatus.WAITING.getStatus());
        insert.setStartDate(startDate);
        hint.setUserId(user.getId());
        hint.setUsername(user.getUsername());
        hint.setStartDate(startDate);
        commandExecDAO.insert(insert);
        this.execId = insert.getId();
        log.info("execHandler-执行命令插入 {}", JSON.toJSONString(insert));
    }

    /**
     * 更新状态
     *
     * @param status status
     */
    protected void updateStatus(ExecStatus status) {
        CommandExecDO updateStatus = new CommandExecDO();
        updateStatus.setId(execId);
        updateStatus.setExecStatus(status.getStatus());
        int effect = commandExecDAO.updateById(updateStatus);
        log.info("execHandler-更新状态 id: {}, status: {}, effect: {}", execId, status, effect);
    }

    @Override
    public void close() {
        Date endDate = new Date();
        // 更新执行时间
        CommandExecDO updateStatus = new CommandExecDO();
        updateStatus.setId(execId);
        updateStatus.setEndDate(endDate);
        commandExecDAO.updateById(updateStatus);
        log.info("execHandler-关闭 id: {}, exitCode: {} endDate: {}; used {} ms", execId, hint.getExitCode(),
                Dates.format(endDate, Dates.YMDHMSS), endDate.getTime() - hint.getStartDate().getTime());
        // 释放资源
        Streams.close(executor);
        Streams.close(hint.getSession());
        execSessionHolder.removeSession(execId);
    }

    public boolean isDone() {
        return executor.isDone();
    }

    public boolean isClose() {
        return executor.isClosed();
    }

}
