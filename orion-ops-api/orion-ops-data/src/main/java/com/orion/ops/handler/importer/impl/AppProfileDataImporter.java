package com.orion.ops.handler.importer.impl;

import com.orion.ops.dao.ApplicationProfileDAO;
import com.orion.ops.entity.domain.ApplicationProfileDO;
import com.orion.ops.entity.importer.DataImportDTO;
import com.orion.ops.service.api.ApplicationProfileService;
import com.orion.spring.SpringHolder;

/**
 * 应用环境 数据导入器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 17:22
 */
public class AppProfileDataImporter extends AbstractDataImporter<ApplicationProfileDO> {

    private static final ApplicationProfileDAO applicationProfileDAO = SpringHolder.getBean(ApplicationProfileDAO.class);

    private static final ApplicationProfileService applicationProfileService = SpringHolder.getBean(ApplicationProfileService.class);

    public AppProfileDataImporter(DataImportDTO importData) {
        super(importData, applicationProfileDAO);
    }

    @Override
    protected void importFinishCallback(boolean isSuccess) {
        applicationProfileService.clearProfileCache();
    }

}
