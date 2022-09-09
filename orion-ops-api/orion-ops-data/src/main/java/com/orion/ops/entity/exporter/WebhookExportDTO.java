package com.orion.ops.entity.exporter;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.ops.constant.webhook.WebhookType;
import com.orion.ops.entity.domain.WebhookConfigDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * webhook 导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 10:16
 */
@Data
@ApiModel(value = "webhook导出")
@ExportTitle(title = "webhook导出")
@ExportSheet(name = "webhook", height = 22, freezeHeader = true, filterHeader = true)
public class WebhookExportDTO {

    @ApiModelProperty(value = "名称")
    @ExportField(index = 0, header = "名称", width = 20, wrapText = true)
    private String name;

    @ApiModelProperty(value = "类型")
    @ExportField(index = 1, header = "类型", width = 20, wrapText = true)
    private String type;

    @ApiModelProperty(value = "url")
    @ExportField(index = 2, header = "url", width = 80, wrapText = true)
    private String url;

    static {
        TypeStore.STORE.register(WebhookConfigDO.class, WebhookExportDTO.class, p -> {
            WebhookExportDTO dto = new WebhookExportDTO();
            dto.setName(p.getWebhookName());
            dto.setType(WebhookType.of(p.getWebhookType()).getLabel());
            dto.setUrl(p.getWebhookUrl());
            return dto;
        });
    }

}
