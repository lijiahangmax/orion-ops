package com.orion.ops.handler.exporter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.ExportType;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.dao.MachineAlarmHistoryDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.MachineAlarmHistoryDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.exporter.MachineAlarmHistoryExportDTO;
import com.orion.ops.entity.request.data.DataExportRequest;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.spring.SpringHolder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 机器报警记录 数据导出器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 10:30
 */
public class MachineAlarmHistoryDataExporter extends AbstractDataExporter<MachineAlarmHistoryExportDTO> {

    private static final MachineInfoDAO machineInfoDAO = SpringHolder.getBean(MachineInfoDAO.class);

    private static final MachineAlarmHistoryDAO machineAlarmHistoryDAO = SpringHolder.getBean(MachineAlarmHistoryDAO.class);

    public MachineAlarmHistoryDataExporter(DataExportRequest request, HttpServletResponse response) {
        super(ExportType.MACHINE_ALARM_HISTORY, request, response);
    }

    @Override
    protected List<MachineAlarmHistoryExportDTO> queryData() {
        // 查询数据
        Long machineId = request.getMachineId();
        LambdaQueryWrapper<MachineAlarmHistoryDO> wrapper = new LambdaQueryWrapper<MachineAlarmHistoryDO>()
                .eq(Objects.nonNull(machineId), MachineAlarmHistoryDO::getMachineId, machineId)
                .orderByDesc(MachineAlarmHistoryDO::getCreateTime);
        List<MachineAlarmHistoryDO> history = machineAlarmHistoryDAO.selectList(wrapper);
        List<MachineAlarmHistoryExportDTO> historyList = Converts.toList(history, MachineAlarmHistoryExportDTO.class);
        // 查询机器名称
        List<Long> machineIdList = historyList.stream()
                .map(MachineAlarmHistoryExportDTO::getMachineId)
                .distinct()
                .collect(Collectors.toList());
        if (!machineIdList.isEmpty()) {
            List<MachineInfoDO> machines = machineInfoDAO.selectNameByIdList(machineIdList);
            for (MachineAlarmHistoryExportDTO record : historyList) {
                machines.stream()
                        .filter(s -> s.getId().equals(record.getMachineId()))
                        .findFirst()
                        .ifPresent(m -> {
                            record.setName(m.getMachineName());
                            record.setHost(m.getMachineHost());
                        });
            }
        }
        return historyList;
    }

    @Override
    protected void setEventParams() {
        super.setEventParams();
        EventParamsHolder.addParam(EventKeys.MACHINE_ID, request.getMachineId());
    }

}
