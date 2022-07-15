package com.orion.ops.entity.dto.importer;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.office.excel.annotation.ImportField;
import com.orion.ops.consts.CnConst;
import com.orion.ops.consts.Const;
import com.orion.ops.entity.domain.ApplicationProfileDO;
import com.orion.ops.entity.vo.DataImportCheckRowVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用环境信息导入
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/1 9:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用环境信息导入")
public class ApplicationProfileImportDTO extends BaseDataImportDTO {

    @ApiModelProperty(value = "环境名称")
    @ImportField(index = 0)
    private String name;

    @ApiModelProperty(value = "唯一标识")
    @ImportField(index = 1)
    private String tag;

    @ApiModelProperty(value = "发布审核")
    @ImportField(index = 2)
    private String releaseAudit;

    @ApiModelProperty(value = "描述")
    @ImportField(index = 3)
    private String description;

    static {
        TypeStore.STORE.register(ApplicationProfileImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.tag);
            vo.setIllegalMessage(p.getIllegalMessage());
            vo.setId(p.getId());
            return vo;
        });
        TypeStore.STORE.register(ApplicationProfileImportDTO.class, ApplicationProfileDO.class, p -> {
            ApplicationProfileDO d = new ApplicationProfileDO();
            d.setId(p.getId());
            d.setProfileName(p.name);
            d.setProfileTag(p.tag);
            d.setReleaseAudit(CnConst.OPEN.equals(p.releaseAudit) ? Const.ENABLE : Const.DISABLE);
            d.setDescription(p.description);
            return d;
        });
    }

}
