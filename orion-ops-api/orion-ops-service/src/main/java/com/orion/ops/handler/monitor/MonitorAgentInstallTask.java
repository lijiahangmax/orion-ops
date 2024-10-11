/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orion.ops.handler.monitor;

import com.orion.lang.constant.Letters;
import com.orion.lang.utils.Arrays1;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.Threads;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.io.Streams;
import com.orion.lang.utils.time.Dates;
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
import lombok.SneakyThrows;
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

    private OutputStream logStream;

    public MonitorAgentInstallTask(Long machineId, UserDTO user) {
        this.machineId = machineId;
        this.user = user;
    }

    @Override
    public void run() {
        log.info("开始安装监控插件 machineId: {}", machineId);
        try {
            // 查询机器信息
            this.machine = machineInfoService.selectById(machineId);
            // 打开日志流
            String logPath = PathBuilders.getInstallLogPath(machineId, MonitorConst.AGENT_FILE_NAME_PREFIX);
            File logFile = new File(Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), logPath));
            Files1.touch(logFile);
            this.logStream = Files1.openOutputStreamFast(logFile);
            // 打开会话
            this.session = machineInfoService.openSessionStore(machineId);
            String pluginDirectory = PathBuilders.getPluginPath(machine.getUsername());
            String startScriptPath = pluginDirectory + "/" + MonitorConst.START_SCRIPT_FILE_NAME;
            // 传输
            this.transferAgentFile(pluginDirectory, startScriptPath);
            // 启动
            this.startAgentApp(startScriptPath);
            // 同步等待
            this.checkAgentRunStatus();
            // 拼接日志
            this.appendLog("安装成功 {}", Dates.current());
        } catch (Exception e) {
            // 拼接日志
            this.appendLog("安装失败 {}", Exceptions.getStackTraceAsString(e));
            // 更新状态
            MachineMonitorDO update = new MachineMonitorDO();
            update.setMonitorStatus(MonitorStatus.NOT_START.getStatus());
            machineMonitorService.updateMonitorConfigByMachineId(machineId, update);
            // 发送站内信
            this.sendWebSideMessage(MessageType.MACHINE_AGENT_INSTALL_FAILURE);
        } finally {
            Streams.close(session);
            Streams.close(logStream);
        }
    }

    /**
     * 传输文件
     *
     * @param pluginDirectory pluginDirectory
     * @param startScriptPath startScriptPath
     */
    private void transferAgentFile(String pluginDirectory, String startScriptPath) {
        // 传输脚本目录
        String agentPath = pluginDirectory + Const.LIB_DIR + "/" + MonitorConst.getAgentFileName();
        SftpExecutor executor = null;
        try {
            // 打开 sftp 连接
            String charset = machineEnvService.getSftpCharset(machineId);
            executor = session.getSftpExecutor(charset);
            executor.connect();
            // 传输启动脚本文件
            String startScript = this.getStartScript(agentPath);
            this.appendLog("开始生成启动脚本 path: {}, command: \n{}", agentPath, startScript);
            executor.write(startScriptPath, Strings.bytes(startScript));
            executor.chmod(startScriptPath, 777);
            // 传输 agent 文件
            File localAgentFile = new File(SystemEnvAttr.MACHINE_MONITOR_AGENT_PATH.getValue());
            // 查询文件是否存在
            long size = executor.getSize(agentPath);
            long totalSize = localAgentFile.length();
            if (totalSize != size) {
                // 传输文件
                this.appendLog("插件包不存在-开始传输 {} {}B", agentPath, totalSize);
                executor.uploadFile(agentPath, localAgentFile);
                this.appendLog("插件包传输完成 {}", agentPath);
            } else {
                this.appendLog("插件包已存在 {}", agentPath);
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
     * @param startScriptPath startScriptPath
     */
    private void startAgentApp(String startScriptPath) {
        CommandExecutor executor = null;
        try {
            // 执行启动命令
            this.appendLog("开始执行启动脚本 path: {}", startScriptPath);
            executor = session.getCommandExecutor("bash -l " + startScriptPath);
            executor.getChannel().setPty(false);
            CommandExecutors.syncExecCommand(executor, logStream);
            int exitCode = executor.getExitCode();
            if (!ExitCode.isSuccess(exitCode)) {
                throw Exceptions.runtime("执行启动失败");
            }
            this.appendLog("命令执行完成 exit: {}", exitCode);
        } catch (Exception e) {
            throw Exceptions.runtime("执行启动异常", e);
        } finally {
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
            Threads.sleep(Const.MS_S_10);
            version = machineMonitorService.syncMonitorAgent(machineId, monitor.getMonitorUrl(), monitor.getAccessToken());
            this.appendLog("检查agent状态 第{}次", i + 1);
            if (version != null) {
                break;
            }
        }
        if (version == null) {
            throw Exceptions.runtime("获取 agent 状态失败");
        }
        this.appendLog("agent启动成功 version: {}", version);
        // 更新状态以及版本
        MachineMonitorDO update = new MachineMonitorDO();
        update.setMonitorStatus(MonitorStatus.RUNNING.getStatus());
        update.setAgentVersion(version);
        machineMonitorService.updateMonitorConfigByMachineId(machineId, update);
        // 发送站内信
        this.sendWebSideMessage(MessageType.MACHINE_AGENT_INSTALL_SUCCESS);
    }

    /**
     * 发送站内信
     */
    private void sendWebSideMessage(MessageType type) {
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.NAME, machine.getMachineName());
        webSideMessageService.addMessage(type, machine.getId(), user.getId(), user.getUsername(), params);
    }

    /**
     * 获取启动脚本
     *
     * @param agentJarPath agentJar 路径
     * @return 脚本内容
     */
    private String getStartScript(String agentJarPath) {
        Map<Object, Object> param = Maps.newMap();
        param.put("processName", MonitorConst.AGENT_FILE_NAME_PREFIX);
        param.put("machineId", machineId);
        param.put("agentJarPath", agentJarPath);
        return Strings.format(MonitorConst.START_SCRIPT_VALUE, param);
    }

    /**
     * 拼接日志
     *
     * @param logString log
     * @param args      args
     */
    @SneakyThrows
    private void appendLog(String logString, Object... args) {
        if (!Arrays1.isEmpty(args)) {
            log.info("安装监控插件-" + logString, args);
        }
        if (logStream != null) {
            logStream.write(Strings.bytes(Strings.format(logString, args)));
            logStream.write(Letters.LF);
            logStream.flush();
        }
    }

}
