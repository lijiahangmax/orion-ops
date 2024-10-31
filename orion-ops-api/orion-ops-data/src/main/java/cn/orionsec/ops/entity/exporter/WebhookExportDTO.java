/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.entity.exporter;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.office.excel.annotation.ExportField;
import cn.orionsec.kit.office.excel.annotation.ExportSheet;
import cn.orionsec.kit.office.excel.annotation.ExportTitle;
import cn.orionsec.ops.constant.webhook.WebhookType;
import cn.orionsec.ops.entity.domain.WebhookConfigDO;
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

    /**
     * @see WebhookType
     */
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
