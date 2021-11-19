package com.orion.ops.handler.exec.impl;

import com.orion.constant.Letters;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.handler.exec.AbstractExecHandler;
import com.orion.ops.handler.exec.ExecHint;
import com.orion.ops.handler.tail.ITailHandler;
import com.orion.ops.handler.tail.TailSessionHolder;
import com.orion.remote.channel.ssh.BaseRemoteExecutor;
import com.orion.spring.SpringHolder;
import com.orion.utils.Strings;
import com.orion.utils.Threads;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Date;

/**
 * 普通命令执行器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/7 17:17
 */
@Slf4j
public class CommandExecHandler extends AbstractExecHandler {

    protected static TailSessionHolder tailSessionHolder = SpringHolder.getBean(TailSessionHolder.class);

    protected String logPath;

    protected OutputStream logOutputStream;

    public CommandExecHandler(ExecHint hint) {
        super(hint);
    }

    @Override
    public void exec() {
        super.exec();
        this.logPath = Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), record.getLogPath());
        Files1.touch(logPath);
    }

    @Override
    protected void openLogger() {
        // 打开日志流
        log.info("execHandler-打开日志流 {} {}", execId, logPath);
        File logFile = new File(logPath);
        this.logOutputStream = Files1.openOutputStreamFastSafe(logFile);
        this.writeExecLogHeader();
    }

    /**
     * 日志初始化完毕 写入数据
     */
    protected void writeExecLogHeader() {
        try {
            StringBuilder sb = new StringBuilder()
                    .append("# 准备执行命令\n")
                    .append("@ssh: ").append(machine.getUsername()).append("@")
                    .append(machine.getMachineHost()).append(":")
                    .append(machine.getSshPort()).append(Letters.LF)
                    .append("执行用户: ").append(record.getUserId()).append(Letters.TAB)
                    .append(record.getUserName()).append(Letters.LF)
                    .append("执行任务: ").append(execId).append(Letters.TAB)
                    .append(hint.getExecType().name()).append(Letters.LF)
                    .append("执行机器: ").append(hint.getMachineId()).append(Letters.TAB)
                    .append(machine.getMachineName()).append(Letters.LF)
                    .append("开始时间: ").append(Dates.format(record.getStartDate(), Dates.YMD_HMS)).append(Letters.LF);
            Long relId = record.getRelId();
            if (relId != null) {
                sb.append("relId: ").append(relId).append(Letters.LF);
            }
            String description = record.getDescription();
            if (!Strings.isBlank(description)) {
                sb.append("描述: ").append(description).append(Letters.LF);
            }
            sb.append(Letters.LF)
                    .append("# 执行命令\n")
                    .append(record.getExecCommand())
                    .append(Letters.LF)
                    .append("# 开始执行\n");
            logOutputStream.write(Strings.bytes(sb.toString()));
            logOutputStream.flush();
        } catch (Exception e) {
            log.error("execHandler-写入日志失败 {} {}", execId, e);
            e.printStackTrace();
        }
    }

    @Override
    protected void processCommandOutputStream(InputStream in) {
        try {
            Streams.transfer(in, logOutputStream);
        } catch (IOException ex) {
            log.error("execHandler-执行命令处理流失败 {} {}", execId, ex);
            ex.printStackTrace();
        }
    }

    @Override
    protected void callback(BaseRemoteExecutor executor) {
        super.callback(executor);
        Date endDate = new Date();
        String interval = Dates.interval(endDate, record.getStartDate(), "d", "h", "m", "s");
        StringBuilder sb = new StringBuilder()
                .append(Letters.LF)
                .append("# 命令执行完毕\n")
                .append("exit code: ").append(record.getExitCode()).append(Letters.LF)
                .append("结束时间: ").append(Dates.format(endDate, Dates.YMD_HMS))
                .append("; used ").append(interval).append(" (")
                .append(endDate.getTime() - record.getStartDate().getTime())
                .append(" ms)\n");
        try {
            logOutputStream.write(Strings.bytes(sb.toString()));
            logOutputStream.flush();
        } catch (Exception e) {
            log.error("execHandler-写入日志失败 {} {}", execId, e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onException(Exception e) {
        super.onException(e);
        StringBuilder sb = new StringBuilder()
                .append("\n--------------------------------------------------\n")
                .append("# 命令执行异常\n");
        try {
            logOutputStream.write(Strings.bytes(sb.toString()));
            e.printStackTrace(new PrintStream(logOutputStream));
            logOutputStream.flush();
        } catch (Exception ex) {
            log.error("execHandler-写入日志失败 {} {}", execId, ex);
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        super.close();
        Streams.close(logOutputStream);
        // 异步关闭正在tail的日志
        Threads.start(() -> {
            try {
                Threads.sleep(Const.MS_S_10);
                tailSessionHolder.getSession(Const.HOST_MACHINE_ID, logPath)
                        .forEach(ITailHandler::close);
            } catch (Exception e) {
                log.error("execHandler-关闭tail失败 {} {}", execId, e);
                e.printStackTrace();
            }
        });
    }

}
