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
package cn.orionsec.ops.runner;

import cn.orionsec.ops.constant.monitor.MonitorConst;
import cn.orionsec.ops.constant.monitor.MonitorStatus;
import cn.orionsec.ops.dao.MachineMonitorDAO;
import cn.orionsec.ops.entity.domain.MachineMonitorDO;
import cn.orionsec.ops.entity.dto.MachineMonitorDTO;
import cn.orionsec.ops.entity.query.MachineMonitorQuery;
import cn.orionsec.ops.service.api.MachineMonitorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.Threads;
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
            // 同步并且获取插件版本
            String monitorVersion = machineMonitorService.syncMonitorAgent(monitor.getMachineId(), monitorUrl, accessToken);
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
