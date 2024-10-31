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

import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.ExportType;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.dao.FileTailListDAO;
import cn.orionsec.ops.dao.MachineInfoDAO;
import cn.orionsec.ops.entity.domain.FileTailListDO;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.exporter.MachineTailFileExportDTO;
import cn.orionsec.ops.entity.request.data.DataExportRequest;
import cn.orionsec.ops.utils.EventParamsHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 机器日志文件 数据导出器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/8 18:38
 */
public class TailFileDataExporter extends AbstractDataExporter<MachineTailFileExportDTO> {

    private static final MachineInfoDAO machineInfoDAO = SpringHolder.getBean(MachineInfoDAO.class);

    private static final FileTailListDAO fileTailListDAO = SpringHolder.getBean(FileTailListDAO.class);

    public TailFileDataExporter(DataExportRequest request, HttpServletResponse response) {
        super(ExportType.TAIL_FILE, request, response);
    }

    @Override
    protected List<MachineTailFileExportDTO> queryData() {
        // 查询数据
        Long queryMachineId = request.getMachineId();
        LambdaQueryWrapper<FileTailListDO> wrapper = new LambdaQueryWrapper<FileTailListDO>()
                .eq(Objects.nonNull(queryMachineId), FileTailListDO::getMachineId, queryMachineId);
        List<FileTailListDO> fileList = fileTailListDAO.selectList(wrapper);
        List<MachineTailFileExportDTO> exportList = Converts.toList(fileList, MachineTailFileExportDTO.class);
        // 机器名称
        List<Long> machineIdList = fileList.stream()
                .map(FileTailListDO::getMachineId)
                .collect(Collectors.toList());
        if (!machineIdList.isEmpty()) {
            List<MachineInfoDO> machineNameList = machineInfoDAO.selectNameByIdList(machineIdList);
            // 设置机器名称
            for (MachineTailFileExportDTO export : exportList) {
                Long machineId = export.getMachineId();
                if (machineId == null) {
                    continue;
                }
                machineNameList.stream()
                        .filter(s -> s.getId().equals(machineId))
                        .findFirst()
                        .ifPresent(s -> {
                            export.setMachineName(s.getMachineName());
                            export.setMachineTag(s.getMachineTag());
                        });
            }
        }
        return exportList;
    }

    @Override
    protected void setEventParams() {
        super.setEventParams();
        EventParamsHolder.addParam(EventKeys.MACHINE_ID, request.getMachineId());
    }

}
