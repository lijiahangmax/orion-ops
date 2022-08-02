package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.Pager;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.convert.Converts;
import com.orion.ops.dao.MachineMonitorDAO;
import com.orion.ops.entity.domain.MachineMonitorDO;
import com.orion.ops.entity.dto.MachineMonitorDTO;
import com.orion.ops.entity.query.MachineMonitorQuery;
import com.orion.ops.entity.request.MachineMonitorRequest;
import com.orion.ops.entity.vo.MachineMonitorVO;
import com.orion.ops.service.api.MachineMonitorService;
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

    // TODO runner

    @Resource
    private MachineMonitorDAO machineMonitorDAO;

    @Override
    public DataGrid<MachineMonitorVO> getMonitorList(MachineMonitorRequest request) {
        Pager<MachineMonitorVO> pager = new Pager<>(request);
        // 参数
        MachineMonitorQuery query = new MachineMonitorQuery();
        query.setMachineId(request.getMachineId());
        query.setMachineName(request.getMachineName());
        query.setInstallStatus(request.getInstallStatus());
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
    public Integer deleteByMachineIdList(List<Long> machineIdList) {
        LambdaQueryWrapper<MachineMonitorDO> wrapper = new LambdaQueryWrapper<MachineMonitorDO>()
                .in(MachineMonitorDO::getMachineId, machineIdList);
        return machineMonitorDAO.delete(wrapper);
    }

}
