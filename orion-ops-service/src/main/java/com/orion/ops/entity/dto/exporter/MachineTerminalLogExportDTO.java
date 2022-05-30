package com.orion.ops.entity.dto.exporter;

import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.office.excel.type.ExcelFieldType;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;

/**
 * 机器终端日志导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/30 10:27
 */
@Data
@ExportTitle(title = "机器终端日志导出")
@ExportSheet(name = "机器终端日志", height = 22, freezeHeader = true, filterHeader = true)
public class MachineTerminalLogExportDTO {

    @ExportField(index = 0, header = "用户名", width = 17, wrapText = true)
    private String username;

    @ExportField(index = 1, header = "机器名称", width = 20, wrapText = true)
    private String machineName;

    @ExportField(index = 2, header = "机器标识", width = 20, wrapText = true)
    private String machineTag;

    @ExportField(index = 3, header = "机器主机", width = 20, wrapText = true)
    private String machineHost;

    @ExportField(index = 4, header = "accessToken", width = 5, hidden = true, wrapText = true)
    private String accessToken;

    @ExportField(index = 5, header = "建立连接时间", width = 20, wrapText = true, type = ExcelFieldType.DATE, format = Dates.YMD_HMS)
    private Date connectedTime;

    @ExportField(index = 6, header = "断开连接时间", width = 20, wrapText = true, type = ExcelFieldType.DATE, format = Dates.YMD_HMS)
    private Date disconnectedTime;

    @ExportField(index = 7, header = "close code", width = 11, wrapText = true)
    private Integer closeCode;

    @ExportField(index = 8, header = "日志文件路径", width = 35, wrapText = true)
    private String logPath;

    static {
        TypeStore.STORE.register(MachineTerminalLogDO.class, MachineTerminalLogExportDTO.class, p -> {
            MachineTerminalLogExportDTO dto = new MachineTerminalLogExportDTO();
            dto.setUsername(p.getUsername());
            dto.setMachineName(p.getMachineName());
            dto.setMachineTag(p.getMachineTag());
            dto.setMachineHost(p.getMachineHost());
            dto.setAccessToken(p.getAccessToken());
            dto.setConnectedTime(p.getConnectedTime());
            dto.setDisconnectedTime(p.getDisconnectedTime());
            dto.setCloseCode(p.getCloseCode());
            dto.setLogPath(p.getOperateLogFile());
            return dto;
        });
    }

}
