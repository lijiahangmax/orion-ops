package com.orion.ops.service.impl;

import com.orion.office.excel.writer.exporting.ExcelExport;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.export.ExportType;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.dto.MachineInfoExportDTO;
import com.orion.ops.entity.request.DataExportRequest;
import com.orion.ops.service.api.DataExportService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.PathBuilders;
import com.orion.utils.Strings;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.FileWriters;
import com.orion.utils.io.Files1;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 数据导出服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 16:18
 */
@Service("dataExportService")
public class DataExportServiceImpl implements DataExportService {

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Override
    public void exportMachine(DataExportRequest request, HttpServletResponse response) throws IOException {
        // 查询数据
        List<MachineInfoDO> machineList = machineInfoDAO.selectList(null);
        List<MachineInfoExportDTO> exportList = Converts.toList(machineList, MachineInfoExportDTO.class);
        if (!Const.ENABLE.equals(request.getExportPassword())) {
            exportList.forEach(s -> s.setEncryptPassword(null));
        }
        // 导出
        ExcelExport<MachineInfoExportDTO> exporter = new ExcelExport<>(MachineInfoExportDTO.class).init();
        exporter.addRows(exportList);
        // 写入
        this.writeWorkbook(request, response, exporter, ExportType.MACHINE);
    }

    /**
     * 写入 workbook
     *
     * @param request  request
     * @param response response
     * @param exporter exporter
     * @param type     type
     * @throws IOException IOException
     */
    private void writeWorkbook(DataExportRequest request, HttpServletResponse response,
                               ExcelExport<?> exporter, ExportType type) throws IOException {
        // 写入 workbook 到 byte
        ByteArrayOutputStream store = new ByteArrayOutputStream();
        String password = request.getProtectPassword();
        if (!Strings.isBlank(password)) {
            exporter.write(store, password);
        } else {
            exporter.write(store);
        }
        // 设置 http 返回
        byte[] excelData = store.toByteArray();
        response.getOutputStream().write(excelData);
        // 保存至本地
        String exportPath = PathBuilders.getExportDataJsonPath(Currents.getUserId(), type.getType(), Strings.def(password));
        String path = Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), exportPath);
        FileWriters.write(path, excelData);
    }

}
