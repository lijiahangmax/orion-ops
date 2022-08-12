package com.orion.ops.handler.monitor;

import com.orion.net.remote.channel.SessionStore;
import com.orion.net.remote.channel.sftp.SftpExecutor;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.utils.PathBuilders;
import com.orion.spring.SpringHolder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

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

    private final Long machineId;

    private SessionStore session;

    private SftpExecutor sftpExecutor;

    public MonitorAgentInstallTask(Long machineId) {
        this.machineId = machineId;
    }

    // TODO 机器环境变量 插件安装目录

    @Override
    public void run() {
        log.info("开始安装监控插件 machineId: {}", machineId);
        MachineInfoDO machine = machineInfoService.selectById(machineId);
        String monitorAgentPath = PathBuilders.getPluginPath(machine.getUsername());
        // 传输文件
        try {
            // 打开会话
            this.session = machineInfoService.openSessionStore(machineId);
            // 打开 sftp 连接
            String charset = machineEnvService.getSftpCharset(machineId);
            this.sftpExecutor = session.getSftpExecutor(charset);
            // 获取本地文件
            File localAgentFile = new File(SystemEnvAttr.MONITOR_AGENT_PATH.getValue());
            // 查询文件是否存在
            long size = sftpExecutor.getSize(monitorAgentPath);
            if (localAgentFile.length() != size) {
                // 传输文件

                // 传输启动脚本

            }
        } catch (Exception e) {

        }
        // 启动
        try {

        }

    }

}
