package com.orion.ops.entity.dto;

import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.ops.consts.machine.MachineAuthType;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 机器信息导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 14:01
 */
@Data
@ExportTitle(title = "机器信息导出")
@ExportSheet(name = "机器信息", height = 22, freezeHeader = true, filterHeader = true)
public class MachineInfoExportDTO {

    /**
     * 机器名称
     */
    @ExportField(index = 0, header = "机器名称", width = 30)
    private String name;

    /**
     * 机器标签
     */
    @ExportField(index = 1, header = "唯一标识", width = 25)
    private String tag;

    /**
     * 机器主机
     */
    @ExportField(index = 2, header = "机器主机", width = 30)
    private String host;

    /**
     * ssh 端口
     */
    @ExportField(index = 3, header = "ssh 端口", width = 10)
    private Integer port;

    /**
     * 用户名
     */
    @ExportField(index = 4, header = "用户名", width = 20)
    private String username;

    /**
     * 密码 (密文)
     */
    @ExportField(index = 5, header = "导出密码 (密文)", width = 20, wrapText = true)
    private String encryptPassword;

    /**
     * 导入密码 (明文)
     */
    @ExportField(index = 6, header = "导入密码 (明文)", width = 20, wrapText = true)
    private String importPassword;

    /**
     * 描述
     */
    @ExportField(index = 7, header = "描述", width = 25, wrapText = true)
    private String description;

    static {
        TypeStore.STORE.register(MachineInfoDO.class, MachineInfoExportDTO.class, p -> {
            MachineInfoExportDTO dto = new MachineInfoExportDTO();
            dto.setName(p.getMachineName());
            dto.setTag(p.getMachineTag());
            dto.setHost(p.getMachineHost());
            dto.setPort(p.getSshPort());
            dto.setUsername(p.getUsername());
            if (MachineAuthType.PASSWORD.getType().equals(p.getAuthType())) {
                dto.setEncryptPassword(p.getPassword());
            }
            dto.setDescription(p.getDescription());
            return dto;
        });
    }

}
