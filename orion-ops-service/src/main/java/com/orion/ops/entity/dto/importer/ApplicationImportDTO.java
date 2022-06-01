package com.orion.ops.entity.dto.importer;

import com.orion.office.excel.annotation.ImportField;
import com.orion.ops.entity.domain.ApplicationInfoDO;
import com.orion.ops.entity.vo.DataImportCheckRowVO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用信息导入
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/1 9:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationImportDTO extends BaseDataImportDTO {

    /**
     * 应用名称
     */
    @ImportField(index = 0)
    private String name;

    /**
     * 唯一标识
     */
    @ImportField(index = 1)
    private String tag;

    /**
     * 应用仓库名称
     */
    @ImportField(index = 2)
    private String vcsName;

    /**
     * 描述
     */
    @ImportField(index = 3)
    private String description;

    /**
     * 仓库id
     */
    private Long vcsId;

    static {
        TypeStore.STORE.register(ApplicationImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.tag);
            vo.setIllegalMessage(p.getIllegalMessage());
            vo.setId(p.getId());
            return vo;
        });
        TypeStore.STORE.register(ApplicationImportDTO.class, ApplicationInfoDO.class, p -> {
            ApplicationInfoDO d = new ApplicationInfoDO();
            d.setId(p.getId());
            d.setAppName(p.name);
            d.setAppTag(p.tag);
            d.setVcsId(p.vcsId);
            d.setDescription(p.description);
            return d;
        });
    }

}
