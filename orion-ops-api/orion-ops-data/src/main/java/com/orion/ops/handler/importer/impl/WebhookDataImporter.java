package com.orion.ops.handler.importer.impl;

import com.orion.ops.dao.WebhookConfigDAO;
import com.orion.ops.entity.domain.WebhookConfigDO;
import com.orion.ops.entity.importer.DataImportDTO;
import com.orion.spring.SpringHolder;

/**
 * webhook 数据导入器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/13 16:21
 */
public class WebhookDataImporter extends AbstractDataImporter<WebhookConfigDO> {

    private static final WebhookConfigDAO webhookConfigDAO = SpringHolder.getBean(WebhookConfigDAO.class);

    public WebhookDataImporter(DataImportDTO importData) {
        super(importData, webhookConfigDAO);
    }

}
