package com.orion.ops.entity.dto.exporter;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.ops.consts.CnConst;
import com.orion.ops.consts.machine.MachineAuthType;
import com.orion.ops.entity.domain.MachineInfoDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机器信息导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 14:01
 */
@Data
@ApiModel(value = "机器信息导出")
@ExportTitle(title = "机器信息导出")
@ExportSheet(name = "机器信息", height = 22, freezeHeader = true, filterHeader = true)
public class MachineInfoExportDTO {

    @ApiModelProperty(value = "机器名称")
    @ExportField(index = 0, header = "机器名称", width = 20, wrapText = true)
    private String name;

    @ApiModelProperty(value = "唯一标识")
    @ExportField(index = 1, header = "唯一标识", width = 20, wrapText = true)
    private String tag;

    @ApiModelProperty(value = "机器主机")
    @ExportField(index = 2, header = "机器主机", width = 20, wrapText = true)
    private String host;

    @ApiModelProperty(value = "ssh 端口")
    @ExportField(index = 3, header = "ssh 端口", width = 10)
    private Integer port;

    @ApiModelProperty(value = "认证方式")
    @ExportField(index = 4, header = "认证方式", width = 13, selectOptions = {CnConst.PASSWORD, CnConst.SECRET_KEY})
    private String authType;

    @ApiModelProperty(value = "用户名")
    @ExportField(index = 5, header = "用户名", width = 20, wrapText = true)
    private String username;

    @ApiModelProperty(value = "导出密码")
    @ExportField(index = 6, header = "导出密码", hidden = true, width = 16, wrapText = true)
    private String encryptPassword;

    @ApiModelProperty(value = "导入密码")
    @ExportField(index = 7, header = "导入密码", width = 16, wrapText = true)
    private String importPassword;

    @ApiModelProperty(value = "描述")
    @ExportField(index = 8, header = "描述", width = 25, wrapText = true)
    private String description;

    static {
        TypeStore.STORE.register(MachineInfoDO.class, MachineInfoExportDTO.class, p -> {
            MachineInfoExportDTO dto = new MachineInfoExportDTO();
            dto.setName(p.getMachineName());
            dto.setTag(p.getMachineTag());
            dto.setHost(p.getMachineHost());
            dto.setPort(p.getSshPort());
            dto.setUsername(p.getUsername());
            MachineAuthType authType = MachineAuthType.of(p.getAuthType());
            if (authType != null) {
                dto.setAuthType(authType.getLabel());
            }
            if (MachineAuthType.PASSWORD.equals(authType)) {
                dto.setEncryptPassword(p.getPassword());
            }
            dto.setDescription(p.getDescription());
            return dto;
        });
    }

}
