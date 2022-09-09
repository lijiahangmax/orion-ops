package com.orion.ops.entity.exporter;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.ops.entity.domain.CommandTemplateDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 命令模板导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/30 10:18
 */
@Data
@ApiModel(value = "命令模板导出")
@ExportTitle(title = "命令模板导出")
@ExportSheet(name = "命令模板", titleHeight = 22, headerHeight = 22, freezeHeader = true, filterHeader = true)
public class CommandTemplateExportDTO {

    @ApiModelProperty(value = "模板名称")
    @ExportField(index = 0, header = "模板名称", width = 25)
    private String name;

    @ApiModelProperty(value = "模板命令")
    @ExportField(index = 1, header = "模板命令", width = 80, wrapText = true)
    private String template;

    @ApiModelProperty(value = "描述")
    @ExportField(index = 2, header = "描述", width = 35, wrapText = true)
    private String description;

    static {
        TypeStore.STORE.register(CommandTemplateDO.class, CommandTemplateExportDTO.class, p -> {
            CommandTemplateExportDTO dto = new CommandTemplateExportDTO();
            dto.setName(p.getTemplateName());
            dto.setTemplate(p.getTemplateValue());
            dto.setDescription(p.getDescription());
            return dto;
        });
    }

}
