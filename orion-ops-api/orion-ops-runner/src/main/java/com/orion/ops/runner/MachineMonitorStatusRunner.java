package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.Threads;
import com.orion.ops.constant.monitor.MonitorConst;
import com.orion.ops.constant.monitor.MonitorStatus;
import com.orion.ops.dao.MachineMonitorDAO;
import com.orion.ops.entity.domain.MachineMonitorDO;
import com.orion.ops.entity.dto.MachineMonitorDTO;
import com.orion.ops.entity.query.MachineMonitorQuery;
import com.orion.ops.service.api.MachineMonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 机器监控插件状态 runner
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/18 18:23
 */
@Component
@Order(4000)
@Slf4j
public class MachineMonitorStatusRunner implements CommandLineRunner {

    @Resource
    private MachineMonitorDAO machineMonitorDAO;

    @Resource
    private MachineMonitorService machineMonitorService;

    @Override
    public void run(String... args) throws Exception {
        log.info("重置机器监控插件状态-开始");
        // 清除启动中的状态
        this.clearStartingStatus();
        // 异步检查插件状态及版本
        Threads.start(this::checkMonitorStatus);
        log.info("重置机器监控插件状态-结束");
    }

    /**
     * 清除启动中的状态
     */
    private void clearStartingStatus() {
        MachineMonitorDO update = new MachineMonitorDO();
        update.setMonitorStatus(MonitorStatus.NOT_START.getStatus());
        update.setUpdateTime(new Date());
        LambdaQueryWrapper<MachineMonitorDO> wrapper = new LambdaQueryWrapper<MachineMonitorDO>()
                .eq(MachineMonitorDO::getMonitorStatus, MonitorStatus.STARTING.getStatus());
        machineMonitorDAO.update(update, wrapper);
    }

    /**
     * 检查插件状态及版本
     */
    private void checkMonitorStatus() {
        // TODO notify
        List<MachineMonitorDTO> monitors = machineMonitorDAO.selectMonitorList(new MachineMonitorQuery(), null);
        for (MachineMonitorDTO monitor : monitors) {
            log.info("检测机器监控插件状态-开始 {} ({})", monitor.getMachineName(), monitor.getMachineHost());
            MachineMonitorDO update = new MachineMonitorDO();
            update.setId(monitor.getId());
            String monitorUrl = monitor.getMonitorUrl();
            String accessToken = monitor.getAccessToken();
            // 不存在则设置默认值
            if (Strings.isBlank(monitorUrl)) {
                monitorUrl = Strings.format(MonitorConst.DEFAULT_URL_FORMAT, monitor.getMachineHost());
                accessToken = MonitorConst.DEFAULT_ACCESS_TOKEN;
                update.setMonitorUrl(monitorUrl);
                update.setAccessToken(accessToken);
            }
            // 获取版本
            String monitorVersion = machineMonitorService.getMonitorVersion(monitorUrl, accessToken);
            if (monitorVersion == null) {
                // 未启动
                update.setMonitorStatus(MonitorStatus.NOT_START.getStatus());
            } else {
                update.setAgentVersion(monitorVersion);
                update.setMonitorStatus(MonitorStatus.RUNNING.getStatus());
            }
            log.info("检测机器监控插件状态-完成 {} ({}), {}", monitor.getMachineName(), monitor.getMachineHost(), update.getMonitorStatus());
            machineMonitorDAO.updateById(update);
        }
    }

}
