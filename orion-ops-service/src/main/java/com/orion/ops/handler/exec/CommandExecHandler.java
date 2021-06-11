package com.orion.ops.handler.exec;

import com.alibaba.fastjson.JSON;
import com.orion.constant.Letters;
import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.command.ExecStatus;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.CommandExecDAO;
import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import com.orion.remote.channel.ssh.BaseRemoteExecutor;
import com.orion.remote.channel.ssh.CommandExecutor;
import com.orion.spring.SpringHolder;
import com.orion.utils.Strings;
import com.orion.utils.Threads;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

/**
 * 命令执行器 基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/7 17:17
 */
@Slf4j
public class CommandExecHandler implements IExecHandler {

    protected static CommandExecDAO commandExecDAO = SpringHolder.getBean("commandExecDAO");

    protected static MachineInfoService machineInfoService = SpringHolder.getBean("machineInfoService");

    protected static MachineEnvService machineEnvService = SpringHolder.getBean("machineEnvService");

    protected static Map<Long, IExecHandler> execSessionHolder = ((ExecSessionHolder) SpringHolder.getBean("execSessionHolder")).getHolder();

    @Getter
    protected ExecHint hint;

    @Getter
    protected Long execId;

    @Getter
    protected CommandExecutor executor;

    protected MachineInfoDO machine;

    protected String logPath;

    protected OutputStream logOutputStream;

    public CommandExecHandler(ExecHint hint) {
        this.hint = hint;
        this.valid();
    }

    @Override
    public Long submit(ExecHint hint) {
        log.info("execHandler-执行命令开始 {} {}", hint.getMachineId(), hint.getExecType());
        CommandExecDO exec = this.convert();
        commandExecDAO.insert(exec);
        this.execId = exec.getId();
        log.info("execHandler-执行命令插入 {}", JSON.toJSONString(exec));
        this.logPath = this.getLogPath();
        CommandExecDO update = new CommandExecDO();
        update.setId(execId);
        update.setLogPath(logPath);
        commandExecDAO.updateById(update);
        this.machine = machineInfoService.selectById(hint.getMachineId());
        Threads.start(this, SchedulerPools.EXEC_SCHEDULER);
        return execId;
    }

    @Override
    public void run() {
        // 打开日志
        this.openLogStream();
        try {
            // 打开commandExecutor
            executor = hint.getSession().getCommandExecutor(hint.getRealCommand());
            execSessionHolder.put(execId, this);
            CommandExecDO updateStatus = new CommandExecDO();
            updateStatus.setId(execId);
            updateStatus.setExecCommand(hint.getRealCommand());
            updateStatus.setExecStatus(ExecStatus.RUNNABLE.getStatus());
            commandExecDAO.updateById(updateStatus);

            // 开始执行
            executor.inherit()
                    .sync()
                    .streamHandler(this::processStandardOutputStream)
                    .callback(this::callback)
                    .connect()
                    .exec();
        } catch (Exception e) {
            e.printStackTrace();
            this.onException(e);
            throw e;
        }
    }

    /**
     * 打开日志流
     */
    protected void openLogStream() {
        log.info("execHandler-打开日志流 {} {}", execId, logPath);
        File logFile = new File(MachineEnvAttr.LOG_PATH.getValue() + logPath);
        this.logOutputStream = Files1.openOutputStreamFastSafe(logFile);
        this.logOpenComputed();
    }

    /**
     * 日志初始化完毕 写入数据
     */
    protected void logOpenComputed() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("# 准备执行命令\n")
                    .append("执行用户: ").append(hint.getUsername()).append(Letters.LF)
                    .append("任务id: ").append(execId).append(Letters.LF)
                    .append("任务类型: ").append(hint.getExecType().name()).append(Letters.LF)
                    .append("机器id: ").append(hint.getMachineId()).append(Letters.LF)
                    .append("机器host: ").append(machine.getMachineHost()).append(Letters.LF)
                    .append("机器user: ").append(machine.getUsername()).append(Letters.LF)
                    .append("机器name: ").append(machine.getMachineName()).append(Letters.LF)
                    .append("\n# 机器环境变量\n");
            Map<String, String> env = this.getEnv();
            env.forEach((k, v) -> sb.append(k).append(" = ").append(v).append(Letters.LF));
            sb.append("\n# 开始执行命令\n")
                    .append(this.replaceCommand(env))
                    .append("\n\n--------------------------------------------------\n");
            logOutputStream.write(Strings.bytes(sb.toString()));
            logOutputStream.flush();
        } catch (Exception e) {
            log.error("execHandler-写入日志失败 {} {}", execId, e);
            e.printStackTrace();
        }
    }

    /**
     * 获取环境变量
     *
     * @return env
     */
    protected Map<String, String> getEnv() {
        Map<String, String> envs = new MutableLinkedHashMap<>();
        // machine env
        Map<String, String> machineEnv = machineEnvService.getMachineEnv(hint.getMachineId());
        machineEnv.forEach((k, v) -> envs.put("env." + k, v));
        // machine
        envs.put("info.host", machine.getMachineHost());
        envs.put("info.port", machine.getSshPort() + Strings.EMPTY);
        envs.put("info.name", machine.getMachineName());
        envs.put("info.tag", machine.getMachineTag());
        envs.put("info.desc", machine.getDescription());
        envs.put("info.username", machine.getUsername());
        envs.put("info.system.type", machine.getSystemType() + Strings.EMPTY);
        return envs;
    }

    /**
     * 替换命令
     *
     * @return command
     */
    protected String replaceCommand(Map<String, String> env) {
        String realCommand = Strings.format(hint.getCommand(), "#", env);
        hint.setRealCommand(realCommand);
        return realCommand;
    }

    /**
     * 处理命令输出
     *
     * @param executor executor
     * @param in       in
     */
    protected void processStandardOutputStream(BaseRemoteExecutor executor, InputStream in) {
        try {
            Streams.transfer(in, logOutputStream);
        } catch (IOException ex) {
            log.error("execHandler-执行命令处理流失败 {} {}", execId, ex);
            ex.printStackTrace();
        }
    }

    /**
     * 完成回调
     *
     * @param executor executor
     */
    protected void callback(BaseRemoteExecutor executor) {
        int exitCode = ((CommandExecutor) executor).getExitCode();
        log.info("execHandler-执行命令完成回调 {} code: {}", execId, exitCode);
        CommandExecDO updateStatus = new CommandExecDO();
        updateStatus.setId(execId);
        updateStatus.setExitCode(exitCode);
        updateStatus.setEndDate(new Date());
        updateStatus.setExecStatus(ExecStatus.COMPLETE.getStatus());
        commandExecDAO.updateById(updateStatus);
        this.close();
    }

    /**
     * 发生异常时调用
     *
     * @param e e
     */
    protected void onException(Exception e) {
        log.error("execHandler-执行命令失败 {} {}", execId, e);
        execSessionHolder.remove(execId);
        CommandExecDO update = new CommandExecDO();
        update.setId(execId);
        update.setExecStatus(ExecStatus.EXCEPTION.getStatus());
        commandExecDAO.updateById(update);
    }

    /**
     * 参数合法校验
     */
    private void valid() {
        Valid.notNull(hint.getExecType());
        Valid.notNull(hint.getMachineId());
        Valid.notBlank(hint.getCommand());
        Valid.notNull(hint.getSession());
    }

    /**
     * hint -> CommandExecDO
     *
     * @return CommandExecDO
     */
    private CommandExecDO convert() {
        UserDTO user = Currents.getUser();
        CommandExecDO insert = new CommandExecDO();
        insert.setUserId(user.getId());
        insert.setUserName(user.getUsername());
        insert.setExecType(hint.getExecType().getType());
        insert.setExecCommand(hint.getCommand());
        insert.setDescription(hint.getDescription());
        insert.setMachineId(hint.getMachineId());
        insert.setExecStatus(ExecStatus.WAITING.getStatus());
        insert.setStartDate(new Date());
        hint.setUserId(user.getId());
        hint.setUsername(user.getUsername());
        return insert;
    }

    /**
     * 获取日志目录
     *
     * @return logPath
     */
    protected String getLogPath() {
        return hint.getExecType().getLogPath() + "/" + execId
                + "_" + hint.getMachineId()
                + "_" + Dates.current(Dates.YMDHMS2) + ".log";
    }

    @Override
    public void close() {
        Streams.close(logOutputStream);
        Streams.close(executor);
        execSessionHolder.remove(execId);
    }

    public boolean isDone() {
        return executor.isDone();
    }

    public boolean isClose() {
        return executor.isClose();
    }

}
