/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
package cn.orionsec.ops.handler.exporter;

import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.ExportType;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.dao.MachineAlarmHistoryDAO;
import cn.orionsec.ops.dao.MachineInfoDAO;
import cn.orionsec.ops.entity.domain.MachineAlarmHistoryDO;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.exporter.MachineAlarmHistoryExportDTO;
import cn.orionsec.ops.entity.request.data.DataExportRequest;
import cn.orionsec.ops.utils.EventParamsHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

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
