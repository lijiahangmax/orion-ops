package com.orion.ops.entity.dto.exporter;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.ops.entity.domain.ApplicationInfoDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用信息导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 15:08
 */
@Data
@ApiModel(value = "应用信息导出")
@ExportTitle(title = "应用信息导出")
@ExportSheet(name = "应用信息", height = 22, freezeHeader = true, filterHeader = true)
public class ApplicationExportDTO {

    @ApiModelProperty(value = "应用名称")
    @ExportField(index = 0, header = "应用名称", width = 35)
    private String name;

    @ApiModelProperty(value = "唯一标识")
    @ExportField(index = 1, header = "唯一标识", width = 30)
    private String tag;

    @ApiModelProperty(value = "应用仓库名称")
    @ExportField(index = 2, header = "应用仓库名称", width = 30)
    private String repoName;

    @ApiModelProperty(value = "描述")
    @ExportField(index = 3, header = "描述", width = 35, wrapText = true)
    private String description;

    @ApiModelProperty(value = "仓库id", hidden = true)
    private Long repoId;

    static {
        TypeStore.STORE.register(ApplicationInfoDO.class, ApplicationExportDTO.class, p -> {
            ApplicationExportDTO dto = new ApplicationExportDTO();
            dto.setName(p.getAppName());
            dto.setTag(p.getAppTag());
            dto.setRepoId(p.getRepoId());
            dto.setDescription(p.getDescription());
            return dto;
        });
    }

}
