package com.orion.ops.handler.exporter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.ExportType;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.dao.FileTailListDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.FileTailListDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.exporter.MachineTailFileExportDTO;
import com.orion.ops.entity.request.data.DataExportRequest;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.spring.SpringHolder;

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
