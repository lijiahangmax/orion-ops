package com.orion.ops.entity.dto.exporter;

import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.ops.entity.domain.ApplicationInfoDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 应用信息导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 15:08
 */
@Data
@ExportTitle(title = "应用信息导出")
@ExportSheet(name = "应用信息", height = 22, freezeHeader = true, filterHeader = true)
public class ApplicationExportDTO {

    /**
     * 应用名称
     */
    @ExportField(index = 0, header = "应用名称", width = 35)
    private String name;

    /**
     * 唯一标识
     */
    @ExportField(index = 1, header = "唯一标识", width = 30)
    private String tag;

    /**
     * 应用仓库名称
     */
    @ExportField(index = 2, header = "应用仓库名称", width = 30)
    private String vcsName;

    /**
     * 描述
     */
    @ExportField(index = 3, header = "描述", width = 35, wrapText = true)
    private String description;

    /**
     * 仓库id
     */
    private Long vcsId;

    static {
        TypeStore.STORE.register(ApplicationInfoDO.class, ApplicationExportDTO.class, p -> {
            ApplicationExportDTO dto = new ApplicationExportDTO();
            dto.setName(p.getAppName());
            dto.setTag(p.getAppTag());
            dto.setVcsId(p.getVcsId());
            dto.setDescription(p.getDescription());
            return dto;
        });
    }

}
