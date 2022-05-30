package com.orion.ops.entity.dto.exporter;

import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.machine.ProxyType;
import com.orion.ops.entity.domain.MachineProxyDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Optional;

/**
 * 机器代理导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/30 10:18
 */
@Data
@ExportTitle(title = "机器代理导出")
@ExportSheet(name = "机器代理", titleHeight = 22, headerHeight = 22, freezeHeader = true, filterHeader = true)
public class MachineProxyExportDTO {

    @ExportField(index = 0, header = "代理主机", width = 25)
    private String host;

    @ExportField(index = 1, header = "代理端口", width = 10)
    private Integer port;

    @ExportField(index = 2, header = "代理类型", width = 13, selectOptions = {Const.PROTOCOL_HTTP, Const.SOCKET4, Const.SOCKET5})
    private String proxyType;

    @ExportField(index = 3, header = "用户名", width = 25)
    private String username;

    @ExportField(index = 4, header = "导出密码 (密文)", width = 15, wrapText = true)
    private String encryptPassword;

    @ExportField(index = 5, header = "导入密码 (明文)", width = 15, wrapText = true)
    private String importPassword;

    @ExportField(index = 6, header = "描述", width = 35, wrapText = true)
    private String description;

    static {
        TypeStore.STORE.register(MachineProxyDO.class, MachineProxyExportDTO.class, p -> {
            MachineProxyExportDTO dto = new MachineProxyExportDTO();
            dto.setHost(p.getProxyHost());
            dto.setPort(p.getProxyPort());
            Optional.ofNullable(p.getProxyType())
                    .map(ProxyType::of)
                    .map(ProxyType::getLabel)
                    .ifPresent(dto::setProxyType);
            dto.setUsername(p.getProxyUsername());
            dto.setEncryptPassword(p.getProxyPassword());
            dto.setDescription(p.getDescription());
            return dto;
        });
    }

}
