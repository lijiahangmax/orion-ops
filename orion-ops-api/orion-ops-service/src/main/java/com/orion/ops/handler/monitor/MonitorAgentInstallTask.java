package com.orion.ops.handler.monitor;

import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.Threads;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.io.Streams;
import com.orion.net.remote.CommandExecutors;
import com.orion.net.remote.ExitCode;
import com.orion.net.remote.channel.SessionStore;
import com.orion.net.remote.channel.sftp.SftpExecutor;
import com.orion.net.remote.channel.ssh.CommandExecutor;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.message.MessageType;
import com.orion.ops.constant.monitor.MonitorConst;
import com.orion.ops.constant.monitor.MonitorStatus;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.domain.MachineMonitorDO;
import com.orion.ops.entity.dto.user.UserDTO;
import com.orion.ops.service.api.*;
import com.orion.ops.utils.PathBuilders;
import com.orion.spring.SpringHolder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

/**
 * 监控插件安装任务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/12 15:35
 */
@Slf4j
public class MonitorAgentInstallTask implements Runnable {

    private static final MachineInfoService machineInfoService = SpringHolder.getBean(MachineInfoService.class);

    private static final MachineEnvService machineEnvService = SpringHolder.getBean(MachineEnvService.class);

    private static final MachineMonitorService machineMonitorService = SpringHolder.getBean(MachineMonitorService.class);

    private static final MachineMonitorEndpointService machineMonitorEndpointService = SpringHolder.getBean(MachineMonitorEndpointService.class);

    private static final WebSideMessageService webSideMessageService = SpringHolder.getBean(WebSideMessageService.class);

    private final Long machineId;

    private final UserDTO user;

    private SessionStore session;

    private MachineInfoDO machine;

    public MonitorAgentInstallTask(Long machineId, UserDTO user) {
        this.machineId = machineId;
        this.user = user;
    }

    @Override
    public void run() {
        log.info("开始安装监控插件 machineId: {}", machineId);
        try {
            this.machine = machineInfoService.selectById(machineId);
            String pluginPath = PathBuilders.getPluginPath(machine.getUsername());
            String agentPath = pluginPath + Const.LIB_DIR + "/" + MonitorConst.AGENT_FILE_NAME;
            // 打开会话
            this.session = machineInfoService.openSessionStore(machineId);
            // 传输
            this.transferAgentFile(agentPath);
            // 启动
            this.startAgentApp(pluginPath, agentPath);
            // 同步等待
            this.checkAgentRunStatus();
        } catch (Exception e) {
            // 更新状态
            MachineMonitorDO update = new MachineMonitorDO();
            update.setMonitorStatus(MonitorStatus.NOT_START.getStatus());
            machineMonitorService.updateMonitorConfigByMachineId(machineId, update);
            // 发送站内信
            this.sendWebsideMessage(MessageType.MACHINE_AGENT_INSTALL_FAILURE);
        } finally {
            Streams.close(session);
        }
    }

    /**
     * 传输文件
     *
     * @param agentPath agentPath
     */
    private void transferAgentFile(String agentPath) {
        SftpExecutor executor = null;
        try {
            // 打开 sftp 连接
            String charset = machineEnvService.getSftpCharset(machineId);
            executor = session.getSftpExecutor(charset);
            executor.connect();
            // 获取本地文件
            File localAgentFile = new File(SystemEnvAttr.MACHINE_MONITOR_AGENT_PATH.getValue());
            // 查询文件是否存在
            long size = executor.getSize(agentPath);
            if (localAgentFile.length() != size) {
                // 传输文件
                executor.uploadFile(agentPath, localAgentFile);
            }
        } catch (Exception e) {
            throw Exceptions.sftp("文件上传失败", e);
        } finally {
            Streams.close(executor);
        }
    }

    /**
     * 启动 agent 应用
     *
     * @param pluginPath pluginPath
     * @param agentPath  agentPath
     */
    private void startAgentApp(String pluginPath, String agentPath) {
        CommandExecutor executor = null;
        OutputStream out = null;
        try {
            // 启动日志目录
            String logPath = PathBuilders.getInstallLogPath(machineId, MonitorConst.AGENT_FILE_NAME_PREFIX);
            File logFile = new File(Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), logPath));
            Files1.touch(logFile);
            out = Files1.openOutputStreamFast(logFile);
            // 执行启动命令
            String script = this.getStartScript(pluginPath, agentPath);
            executor = session.getCommandExecutor(script);
            executor.getChannel().setPty(false);
            CommandExecutors.syncExecCommand(executor, out);
            int exitCode = executor.getExitCode();
            if (!ExitCode.isSuccess(exitCode)) {
                throw Exceptions.runtime("执行启动失败");
            }
        } catch (Exception e) {
            throw Exceptions.runtime("执行启动异常", e);
        } finally {
            Streams.close(out);
            Streams.close(executor);
        }
    }

    /**
     * 同步检查 agent 状态
     */
    private void checkAgentRunStatus() {
        // 查询配置
        MachineMonitorDO monitor = machineMonitorService.selectByMachineId(machineId);
        // 尝试进行同步 检查是否启动
        String version = null;
        for (int i = 0; i < 5; i++) {
            try {
                Threads.sleep(Const.MS_S_10);
                version = machineMonitorService.syncMonitorAgent(machineId, monitor.getMonitorUrl(), monitor.getAccessToken());
                break;
            } catch (Exception e) {
                // ignore
            }
        }
        if (version == null) {
            throw Exceptions.runtime("获取 agent 状态失败");
        }
        // 更新状态以及版本
        MachineMonitorDO update = new MachineMonitorDO();
        update.setMonitorStatus(MonitorStatus.RUNNING.getStatus());
        update.setAgentVersion(version);
        machineMonitorService.updateMonitorConfigByMachineId(machineId, update);
        // 发送站内信
        this.sendWebsideMessage(MessageType.MACHINE_AGENT_INSTALL_SUCCESS);
    }

    /**
     * 发送站内信
     */
    private void sendWebsideMessage(MessageType type) {
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.NAME, machine.getMachineName());
        webSideMessageService.addMessage(type, user.getId(), user.getUsername(), params);
    }

    /**
     * 获取启动脚本
     *
     * @param pluginPath   文件创建路径
     * @param agentJarPath agentJar 路径
     * @return 脚本内容
     */
    private String getStartScript(String pluginPath, String agentJarPath) {
        Map<Object, Object> param = Maps.newMap();
        param.put("killTag", MonitorConst.AGENT_FILE_NAME_PREFIX);
        param.put("machineId", machineId);
        param.put("pluginPath", pluginPath);
        param.put("agentJarPath", agentJarPath);
        param.put("scriptPath", pluginPath + "/" + MonitorConst.START_SCRIPT_FILE_NAME);
        param.put("logPath", pluginPath + "/" + MonitorConst.AGENT_LOG_FILE_NAME);
        return Strings.format(MonitorConst.START_SCRIPT_VALUE, param);
    }

}
