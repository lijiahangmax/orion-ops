package com.orion.ops.entity.importer;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.office.excel.annotation.ImportField;
import com.orion.ops.constant.webhook.WebhookType;
import com.orion.ops.entity.domain.WebhookConfigDO;
import com.orion.ops.entity.vo.data.DataImportCheckRowVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

/**
 * webhook 导入
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/13 16:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "webhook 导入")
public class WebhookImportDTO extends BaseDataImportDTO {

    @ApiModelProperty(value = "名称")
    @ImportField(index = 0)
    private String name;

    /**
     * @see com.orion.ops.constant.webhook.WebhookType
     */
    @ApiModelProperty(value = "类型")
    @ImportField(index = 1)
    private String type;

    @ApiModelProperty(value = "url")
    @ImportField(index = 2)
    private String url;

    static {
        TypeStore.STORE.register(WebhookImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.name);
            vo.setIllegalMessage(p.getIllegalMessage());
            vo.setId(p.getId());
            return vo;
        });
        TypeStore.STORE.register(WebhookImportDTO.class, WebhookConfigDO.class, p -> {
            WebhookConfigDO d = new WebhookConfigDO();
            d.setId(p.getId());
            d.setWebhookName(p.name);
            d.setWebhookUrl(p.url);
            Optional.ofNullable(p.type)
                    .map(WebhookType::of)
                    .map(WebhookType::getType)
                    .ifPresent(d::setWebhookType);
            return d;
        });
    }

}
