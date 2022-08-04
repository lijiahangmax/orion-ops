package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.define.wrapper.Pager;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.monitor.MonitorStatus;
import com.orion.ops.constant.monitor.MonitorConst;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.dao.MachineMonitorDAO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.domain.MachineMonitorDO;
import com.orion.ops.entity.dto.MachineMonitorDTO;
import com.orion.ops.entity.query.MachineMonitorQuery;
import com.orion.ops.entity.request.MachineMonitorRequest;
import com.orion.ops.entity.vo.MachineMonitorVO;
import com.orion.ops.handler.http.MachineMonitorHttpApi;
import com.orion.ops.handler.http.MachineMonitorHttpApiRequester;
import com.orion.ops.service.api.MachineMonitorService;
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
        // 查询
        MachineMonitorDO monitor = this.selectByMachineId(machineId);
        if (monitor != null) {
            return Converts.to(monitor, MachineMonitorVO.class);
        }
        // 查询机器
        MachineInfoDO machine = machineInfoDAO.selectById(machineId);
        Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        // 不存在则插入
        MachineMonitorDO insert = new MachineMonitorDO();
        insert.setMachineId(machineId);
        insert.setMonitorStatus(MonitorStatus.NOT_INSTALL.getStatus());
        insert.setMonitorUrl(Strings.format(MonitorConst.DEFAULT_URL_FORMAT, machine.getMachineHost()));
        insert.setAccessToken(MonitorConst.DEFAULT_ACCESS_TOKEN);
        machineMonitorDAO.insert(insert);
        return Converts.to(insert, MachineMonitorVO.class);
    }

    @Override
    public Integer updateMonitorConfig(MachineMonitorRequest request) {
        // 查询
        Long id = request.getId();
        MachineMonitorDO monitor = machineMonitorDAO.selectById(id);
        Valid.notNull(monitor, MessageConst.CONFIG_ABSENT);
        // 更新
        MachineMonitorDO update = new MachineMonitorDO();
        update.setId(id);
        update.setMonitorUrl(request.getUrl());
        update.setAccessToken(request.getAccessToken());
        return machineMonitorDAO.updateById(update);
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
    public Integer deleteByMachineIdList(List<Long> machineIdList) {
        LambdaQueryWrapper<MachineMonitorDO> wrapper = new LambdaQueryWrapper<MachineMonitorDO>()
                .in(MachineMonitorDO::getMachineId, machineIdList);
        return machineMonitorDAO.delete(wrapper);
    }

}
