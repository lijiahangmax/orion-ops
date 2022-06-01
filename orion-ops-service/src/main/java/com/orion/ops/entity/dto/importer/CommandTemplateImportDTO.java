package com.orion.ops.entity.dto.importer;

import com.orion.office.excel.annotation.ImportField;
import com.orion.ops.entity.domain.CommandTemplateDO;
import com.orion.ops.entity.vo.DataImportCheckRowVO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 命令模板导入
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/1 9:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommandTemplateImportDTO extends BaseDataImportDTO {

    /**
     * 模板名称
     */
    @ImportField(index = 0)
    private String name;

    /**
     * 模板命令
     */
    @ImportField(index = 1)
    private String template;

    /**
     * 描述
     */
    @ImportField(index = 2)
    private String description;

    static {
        TypeStore.STORE.register(CommandTemplateImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.name);
            vo.setIllegalMessage(p.getIllegalMessage());
            vo.setId(p.getId());
            return vo;
        });
        TypeStore.STORE.register(CommandTemplateImportDTO.class, CommandTemplateDO.class, p -> {
            CommandTemplateDO d = new CommandTemplateDO();
            d.setId(p.getId());
            d.setTemplateName(p.name);
            d.setTemplateValue(p.template);
            d.setDescription(p.description);
            return d;
        });
    }

}
