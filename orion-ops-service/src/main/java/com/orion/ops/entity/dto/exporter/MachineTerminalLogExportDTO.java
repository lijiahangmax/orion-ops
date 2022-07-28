package com.orion.ops.entity.dto.exporter;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.office.excel.type.ExcelFieldType;
import com.orion.ops.entity.domain.MachineTerminalLogDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 终端日志导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/30 10:27
 */
@Data
@ApiModel(value = "终端日志导出")
@ExportTitle(title = "终端日志导出")
@ExportSheet(name = "终端日志", height = 22, freezeHeader = true, filterHeader = true)
public class MachineTerminalLogExportDTO {

    @ApiModelProperty(value = "用户名")
    @ExportField(index = 0, header = "用户名", width = 17, wrapText = true)
    private String username;

    @ApiModelProperty(value = "机器名称")
    @ExportField(index = 1, header = "机器名称", width = 20, wrapText = true)
    private String machineName;

    @ApiModelProperty(value = "机器标识")
    @ExportField(index = 2, header = "机器标识", width = 20, wrapText = true)
    private String machineTag;

    @ApiModelProperty(value = "机器主机")
    @ExportField(index = 3, header = "机器主机", width = 20, wrapText = true)
    private String machineHost;

    @ApiModelProperty(value = "accessToken")
    @ExportField(index = 4, header = "accessToken", width = 5, hidden = true, wrapText = true)
    private String accessToken;

    @ApiModelProperty(value = "建立连接时间")
    @ExportField(index = 5, header = "建立连接时间", width = 20, wrapText = true, type = ExcelFieldType.DATE, format = Dates.YMD_HMS)
    private Date connectedTime;

    @ApiModelProperty(value = "断开连接时间")
    @ExportField(index = 6, header = "断开连接时间", width = 20, wrapText = true, type = ExcelFieldType.DATE, format = Dates.YMD_HMS)
    private Date disconnectedTime;

    @ApiModelProperty(value = "close code")
    @ExportField(index = 7, header = "close code", width = 11, wrapText = true)
    private Integer closeCode;

    @ApiModelProperty(value = "录屏文件路径")
    @ExportField(index = 8, header = "录屏文件路径", width = 35, wrapText = true)
    private String screenPath;

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
            dto.setScreenPath(p.getScreenPath());
            return dto;
        });
    }

}
