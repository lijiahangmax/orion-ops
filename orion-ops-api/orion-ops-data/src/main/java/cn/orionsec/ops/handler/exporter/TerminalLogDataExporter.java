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
package cn.orionsec.ops.handler.exporter;

import cn.orionsec.ops.constant.ExportType;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.dao.MachineTerminalLogDAO;
import cn.orionsec.ops.entity.domain.MachineTerminalLogDO;
import cn.orionsec.ops.entity.exporter.MachineTerminalLogExportDTO;
import cn.orionsec.ops.entity.request.data.DataExportRequest;
import cn.orionsec.ops.utils.EventParamsHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.convert.Converts;
import com.orion.spring.SpringHolder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * 机器终端日志 数据导出器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/8 18:33
 */
public class TerminalLogDataExporter extends AbstractDataExporter<MachineTerminalLogExportDTO> {

    private static final MachineTerminalLogDAO machineTerminalLogDAO = SpringHolder.getBean(MachineTerminalLogDAO.class);

    public TerminalLogDataExporter(DataExportRequest request, HttpServletResponse response) {
        super(ExportType.TERMINAL_LOG, request, response);
    }

    @Override
    protected List<MachineTerminalLogExportDTO> queryData() {
        // 查询数据
        Long machineId = request.getMachineId();
        LambdaQueryWrapper<MachineTerminalLogDO> wrapper = new LambdaQueryWrapper<MachineTerminalLogDO>()
                .eq(Objects.nonNull(machineId), MachineTerminalLogDO::getMachineId, machineId)
                .orderByDesc(MachineTerminalLogDO::getCreateTime);
        List<MachineTerminalLogDO> terminalList = machineTerminalLogDAO.selectList(wrapper);
        return Converts.toList(terminalList, MachineTerminalLogExportDTO.class);
    }

    @Override
    protected void setEventParams() {
        super.setEventParams();
        EventParamsHolder.addParam(EventKeys.MACHINE_ID, request.getMachineId());
    }

}
