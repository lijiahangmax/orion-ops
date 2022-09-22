package com.orion.ops.handler.importer.impl;

import com.orion.ops.dao.ApplicationInfoDAO;
import com.orion.ops.entity.domain.ApplicationInfoDO;
import com.orion.ops.entity.importer.DataImportDTO;
import com.orion.ops.service.api.ApplicationInfoService;
import com.orion.spring.SpringHolder;

/**
 * 应用信息 数据导入器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 17:24
 */
public class ApplicationDataImporter extends AbstractDataImporter<ApplicationInfoDO> {

    private static final ApplicationInfoDAO applicationInfoDAO = SpringHolder.getBean(ApplicationInfoDAO.class);

    private static final ApplicationInfoService applicationInfoService = SpringHolder.getBean(ApplicationInfoService.class);

    public ApplicationDataImporter(DataImportDTO importData) {
        super(importData, applicationInfoDAO);
    }

    @Override
    protected void insertFiller(ApplicationInfoDO row) {
        row.setAppSort(applicationInfoService.getNextSort());
    }

}
