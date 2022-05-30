package com.orion.ops.entity.dto.exporter;

import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.office.excel.type.ExcelAlignType;
import com.orion.ops.consts.CnConst;
import com.orion.ops.consts.Const;
import com.orion.ops.entity.domain.ApplicationProfileDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 应用环境信息导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/29 14:48
 */
@Data
@ExportTitle(title = "应用环境导出")
@ExportSheet(name = "应用环境", height = 22, freezeHeader = true, filterHeader = true)
public class ApplicationProfileExportDTO {

    /**
     * 环境名称
     */
    @ExportField(index = 0, header = "环境名称", width = 30)
    private String name;

    /**
     * 唯一标识
     */
    @ExportField(index = 1, header = "唯一标识", width = 30)
    private String tag;

    /**
     * 发布审核
     */
    @ExportField(index = 2, header = "发布审核(开启/关闭)", width = 23, align = ExcelAlignType.CENTER, selectOptions = {CnConst.OPEN, CnConst.CLOSE})
    private String releaseAudit;

    /**
     * 描述
     */
    @ExportField(index = 3, header = "描述", width = 35, wrapText = true)
    private String description;

    static {
        TypeStore.STORE.register(ApplicationProfileDO.class, ApplicationProfileExportDTO.class, p -> {
            ApplicationProfileExportDTO dto = new ApplicationProfileExportDTO();
            dto.setName(p.getProfileName());
            dto.setTag(p.getProfileTag());
            dto.setReleaseAudit(Const.ENABLE.equals(p.getReleaseAudit()) ? CnConst.OPEN : CnConst.CLOSE);
            dto.setDescription(p.getDescription());
            return dto;
        });
    }

}
