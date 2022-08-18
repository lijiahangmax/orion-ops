package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.define.wrapper.Pager;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.Threads;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.io.Files1;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.SchedulerPools;
import com.orion.ops.constant.monitor.MonitorConst;
import com.orion.ops.constant.monitor.MonitorStatus;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.dao.MachineMonitorDAO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.domain.MachineMonitorDO;
import com.orion.ops.entity.dto.MachineMonitorDTO;
import com.orion.ops.entity.query.MachineMonitorQuery;
import com.orion.ops.entity.request.machine.MachineMonitorRequest;
import com.orion.ops.entity.vo.machine.MachineMonitorVO;
import com.orion.ops.handler.http.MachineMonitorHttpApi;
import com.orion.ops.handler.http.MachineMonitorHttpApiRequester;
import com.orion.ops.handler.monitor.MonitorAgentInstallTask;
import com.orion.ops.service.api.MachineMonitorService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 机器监控服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 18:52
 */
@Service("machineMonitorService")
public class MachineMonitorServiceImpl implements MachineMonitorService {

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private MachineMonitorDAO machineMonitorDAO;

    @Override
    public MachineMonitorDO selectByMachineId(Long machineId) {
        LambdaQueryWrapper<MachineMonitorDO> wrapper = new LambdaQueryWrapper<MachineMonitorDO>()
                .eq(MachineMonitorDO::getMachineId, machineId);
        return machineMonitorDAO.selectOne(wrapper);
    }

    @Override
    public DataGrid<MachineMonitorVO> getMonitorList(MachineMonitorRequest request) {
        Pager<MachineMonitorVO> pager = new Pager<>(request);
        // 参数
        MachineMonitorQuery query = new MachineMonitorQuery();
        query.setMachineId(request.getMachineId());
        query.setMachineName(request.getMachineName());
        query.setMonitorStatus(request.getStatus());
        // 查询数量
        Integer count = machineMonitorDAO.selectMonitorCount(query);
        pager.setTotal(count);
        if (pager.hasMoreData()) {
            // 查询数据
            List<MachineMonitorDTO> rows = machineMonitorDAO.selectMonitorList(query, pager.getSql());
            pager.setRows(Converts.toList(rows, MachineMonitorVO.class));
        } else {
            pager.setRows(Lists.empty());
        }
        return DataGrid.of(pager);
    }

    @Override
    public MachineMonitorVO getMonitorConfig(Long machineId) {
        // 查询机器
        MachineInfoDO machine = machineInfoDAO.selectById(machineId);
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        // 查询
        MachineMonitorDO monitor = this.selectByMachineId(machineId);
        if (monitor == null) {
            // 不存在则插入
            monitor = new MachineMonitorDO();
            monitor.setMachineId(machineId);
            monitor.setMonitorStatus(MonitorStatus.NOT_START.getStatus());
            monitor.setMonitorUrl(Strings.format(MonitorConst.DEFAULT_URL_FORMAT, machine.getMachineHost()));
            monitor.setAccessToken(MonitorConst.DEFAULT_ACCESS_TOKEN);
            machineMonitorDAO.insert(monitor);
        }
        MachineMonitorVO vo = Converts.to(monitor, MachineMonitorVO.class);
        vo.setMachineName(machine.getMachineName());
        vo.setMachineHost(machine.getMachineHost());
        return vo;
    }

    @Override
    public Integer updateMonitorConfig(MachineMonitorRequest request) {
        // 查询
        Long id = request.getId();
        String url = request.getUrl();
        String accessToken = request.getAccessToken();
        MachineMonitorDO monitor = machineMonitorDAO.selectById(id);
        Valid.notNull(monitor, MessageConst.CONFIG_ABSENT);
        // 更新
        MachineMonitorDO update = new MachineMonitorDO();
        update.setId(id);
        update.setMonitorUrl(url);
        update.setAccessToken(accessToken);
        // ping
        if (monitor.getMonitorStatus().equals(MonitorStatus.NOT_START.getStatus()) ||
                monitor.getMonitorStatus().equals(MonitorStatus.RUNNING.getStatus())) {
            // 获取版本
            String monitorVersion = this.getMonitorVersion(url, accessToken);
            if (monitorVersion == null) {
                // 未启动
                update.setMonitorStatus(MonitorStatus.NOT_START.getStatus());
            } else {
                update.setAgentVersion(monitorVersion);
                update.setMonitorStatus(MonitorStatus.RUNNING.getStatus());
            }
        }
        return machineMonitorDAO.updateById(update);
    }

    @Override
    public Integer updateMonitorConfigByMachineId(Long machineId, MachineMonitorDO update) {
        LambdaQueryWrapper<MachineMonitorDO> wrapper = new LambdaQueryWrapper<MachineMonitorDO>()
                .eq(MachineMonitorDO::getMachineId, machineId);
        return machineMonitorDAO.update(update, wrapper);
    }

    @Override
    public Integer installMonitorAgent(Long machineId) {
        // 查询
        MachineMonitorVO config = this.getMonitorConfig(machineId);
        Valid.eq(config.getStatus(), MonitorStatus.NOT_START.getStatus(), MessageConst.AGENT_NOT_IS_NOT_START);
        // 修改状态
        MachineMonitorDO update = new MachineMonitorDO();
        update.setId(config.getId());
        try {
            // 尝试 ping
            HttpWrapper<Integer> pingWrapper = this.testPingMonitor(config.getUrl(), config.getAccessToken());
            Valid.isTrue(pingWrapper.isOk());
            // 可以 ping 通状态改为已启动
            update.setMonitorStatus(MonitorStatus.RUNNING.getStatus());
            machineMonitorDAO.updateById(update);
        } catch (Exception e) {
            // ping 不通检查文件是否存在
            String path = SystemEnvAttr.MACHINE_MONITOR_AGENT_PATH.getValue();
            Valid.isTrue(Files1.isFile(path), Strings.format(MessageConst.AGENT_FILE_NON_EXIST, path));
            // 状态改为启动中
            update.setMonitorStatus(MonitorStatus.STARTING.getStatus());
            machineMonitorDAO.updateById(update);
            // 创建安装任务
            Threads.start(new MonitorAgentInstallTask(machineId, Currents.getUser()), SchedulerPools.AGENT_INSTALL_SCHEDULER);
        }
        return update.getMonitorStatus();
    }

    @Override
    public Integer deleteByMachineIdList(List<Long> machineIdList) {
        LambdaQueryWrapper<MachineMonitorDO> wrapper = new LambdaQueryWrapper<MachineMonitorDO>()
                .in(MachineMonitorDO::getMachineId, machineIdList);
        return machineMonitorDAO.delete(wrapper);
    }

    @Override
    public HttpWrapper<Integer> testPingMonitor(String url, String accessToken) {
        return MachineMonitorHttpApiRequester.builder()
                .url(url)
                .accessToken(accessToken)
                .api(MachineMonitorHttpApi.ENDPOINT_PING)
                .build()
                .request(Integer.class);
    }

    @Override
    public void setVersionAndStatus(Long id) {
        MachineMonitorDO monitor = machineMonitorDAO.selectById(id);
        if (monitor.getMonitorStatus().equals(MonitorStatus.STARTING.getStatus())) {
            return;
        }
        MachineMonitorDO update = new MachineMonitorDO();
        update.setId(id);
        // 获取版本
        String monitorVersion = this.getMonitorVersion(monitor.getMonitorUrl(), monitor.getAccessToken());
        if (monitorVersion == null) {
            // 未启动
            update.setMonitorStatus(MonitorStatus.NOT_START.getStatus());
        } else {
            update.setAgentVersion(monitorVersion);
            update.setMonitorStatus(MonitorStatus.RUNNING.getStatus());
        }
        machineMonitorDAO.updateById(update);
    }

    /**
     * 获取版本
     *
     * @param url         url
     * @param accessToken accessToken
     * @return version
     */
    private String getMonitorVersion(String url, String accessToken) {
        try {
            return MachineMonitorHttpApiRequester.builder()
                    .url(url)
                    .accessToken(accessToken)
                    .api(MachineMonitorHttpApi.ENDPOINT_VERSION)
                    .build()
                    .request(String.class)
                    .getData();
        } catch (Exception e) {
            return null;
        }
    }

}
