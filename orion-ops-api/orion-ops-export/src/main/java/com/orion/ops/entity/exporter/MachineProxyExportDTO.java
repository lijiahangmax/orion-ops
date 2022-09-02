package com.orion.ops.entity.exporter;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.machine.ProxyType;
import com.orion.ops.entity.domain.MachineProxyDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "机器代理导出")
@ExportTitle(title = "机器代理导出")
@ExportSheet(name = "机器代理", height = 22, freezeHeader = true, filterHeader = true)
public class MachineProxyExportDTO {

    @ApiModelProperty(value = "代理主机")
    @ExportField(index = 0, header = "代理主机", width = 25)
    private String host;

    @ApiModelProperty(value = "代理端口")
    @ExportField(index = 1, header = "代理端口", width = 10)
    private Integer port;

    @ApiModelProperty(value = "代理类型")
    @ExportField(index = 2, header = "代理类型", width = 13, selectOptions = {Const.PROTOCOL_HTTP, Const.SOCKET4, Const.SOCKET5})
    private String proxyType;

    @ApiModelProperty(value = "用户名")
    @ExportField(index = 3, header = "用户名", width = 25)
    private String username;

    @ApiModelProperty(value = "导出密码")
    @ExportField(index = 4, header = "导出密码", hidden = true, width = 16, wrapText = true)
    private String encryptPassword;

    @ApiModelProperty(value = "导入密码")
    @ExportField(index = 5, header = "导入密码", width = 16, wrapText = true)
    private String importPassword;

    @ApiModelProperty(value = "描述")
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
