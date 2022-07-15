package com.orion.ops.handler.app.action;

import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.io.Streams;
import com.orion.net.remote.CommandExecutors;
import com.orion.net.remote.ExitCode;
import com.orion.net.remote.channel.SessionStore;
import com.orion.net.remote.channel.ssh.CommandExecutor;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.StainCode;
import com.orion.ops.consts.command.CommandConst;
import com.orion.ops.consts.env.EnvConst;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.utils.Utils;
import com.orion.spring.SpringHolder;
import lombok.Getter;

import java.io.File;
import java.util.Map;

/**
 * 执行操作-传输产物 scp 方式
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.consts.app.ActionType#RELEASE_TRANSFER
 * @see com.orion.ops.consts.app.TransferMode#SCP
 * @since 2022/4/26 23:58
 */
public class ScpTransferActionHandler extends AbstractActionHandler {

    protected static MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    private SessionStore session;

    private CommandExecutor executor;

    @Getter
    private Integer exitCode;

    public ScpTransferActionHandler(Long actionId, MachineActionStore store) {
        super(actionId, store);
    }

    @Override
    protected void handler() throws Exception {
        // 检查文件
        String bundlePath = Files1.getPath(SystemEnvAttr.DIST_PATH.getValue(), store.getBundlePath());
        File bundleFile = new File(bundlePath);
        if (!bundleFile.exists()) {
            throw Exceptions.log("*** 产物文件不存在 " + bundlePath);
        }
        // 替换命令
        String scpCommand = Strings.def(action.getActionCommand(), CommandConst.SCP_TRANSFER_DEFAULT);
        Map<String, String> params = Maps.newMap();
        params.put(EnvConst.BUNDLE_PATH, bundlePath);
        // 目标文件有空格需要转义空格为 \\
        params.put(EnvConst.TRANSFER_PATH, store.getTransferPath().replaceAll(Strings.SPACE, "\\\\\\\\ "));
        params.put(EnvConst.TARGET_USERNAME, store.getMachineUsername());
        params.put(EnvConst.TARGET_HOST, store.getMachineHost());
        scpCommand = Strings.format(scpCommand, EnvConst.SYMBOL, params);
        // 拼接日志
        StringBuilder log = new StringBuilder(Const.LF)
                .append(Utils.getStainKeyWords("# 执行 scp 传输命令", StainCode.GLOSS_BLUE))
                .append(Const.LF)
                .append(StainCode.prefix(StainCode.GLOSS_CYAN))
                .append(Utils.getEndLfWithEof(scpCommand))
                .append(StainCode.SUFFIX);
        this.appendLog(log.toString());
        // 打开session
        this.session = machineInfoService.openSessionStore(Const.HOST_MACHINE_ID);
        // 打开executor
        this.executor = session.getCommandExecutor(Strings.replaceCRLF(scpCommand));
        // 执行命令
        CommandExecutors.syncExecCommand(executor, appender);
        this.exitCode = executor.getExitCode();
        this.appendLog(Const.LF);
        if (!ExitCode.isSuccess(exitCode)) {
            throw Exceptions.execute("*** 命令执行失败 exitCode: " + exitCode);
        }
    }

    @Override
    public void write(String command) {
        executor.write(command);
    }

    @Override
    public void terminate() {
        super.terminate();
        // 关闭executor
        Streams.close(executor);
        // 关闭宿主机session
        Streams.close(session);
    }

    @Override
    public void close() {
        super.close();
        // 关闭executor
        Streams.close(executor);
        // 关闭宿主机session
        Streams.close(session);
    }

}
