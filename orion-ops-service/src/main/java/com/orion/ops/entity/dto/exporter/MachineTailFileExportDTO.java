package com.orion.ops.entity.dto.exporter;

import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.ops.entity.domain.FileTailListDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 机器日志文件导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/30 10:40
 */
@Data
@ExportTitle(title = "机器日志文件导出")
@ExportSheet(name = "机器日志文件", titleHeight = 22, headerHeight = 22, freezeHeader = true, filterHeader = true)
public class MachineTailFileExportDTO {

    @ExportField(index = 0, header = "机器名称", width = 20, wrapText = true)
    private String machineName;

    @ExportField(index = 1, header = "机器标识", width = 20, wrapText = true)
    private String machineTag;

    @ExportField(index = 2, header = "别名", width = 20, wrapText = true)
    private String name;

    @ExportField(index = 3, header = "文件路径", width = 35, wrapText = true)
    private String path;

    @ExportField(index = 4, header = "文件编码", width = 12)
    private String charset;

    @ExportField(index = 5, header = "尾部偏移行", width = 12)
    private Integer offset;

    @ExportField(index = 6, header = "tail 命令", width = 35, wrapText = true)
    private String command;

    /**
     * 机器id
     */
    private Long machineId;

    static {
        TypeStore.STORE.register(FileTailListDO.class, MachineTailFileExportDTO.class, p -> {
            MachineTailFileExportDTO dto = new MachineTailFileExportDTO();
            dto.setMachineId(p.getMachineId());
            dto.setName(p.getAliasName());
            dto.setPath(p.getFilePath());
            dto.setCharset(p.getFileCharset());
            dto.setOffset(p.getFileOffset());
            dto.setCommand(p.getTailCommand());
            return dto;
        });
    }

}
