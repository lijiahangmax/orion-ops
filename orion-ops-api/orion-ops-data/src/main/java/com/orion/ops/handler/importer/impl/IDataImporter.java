package com.orion.ops.handler.importer.impl;

import com.orion.lang.utils.Exceptions;
import com.orion.ops.constant.ImportType;
import com.orion.ops.entity.importer.DataImportDTO;

/**
 * 数据导入器 接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 16:49
 */
public interface IDataImporter {

    /**
     * 执行导入
     */
    void doImport();

    /**
     * 创建数据导入器
     *
     * @param importData importData
     * @return importer
     */
    static IDataImporter create(DataImportDTO importData) {
        switch (ImportType.of(importData.getType())) {
            case MACHINE_INFO:
                return new MachineInfoDataImporter(importData);
            case MACHINE_PROXY:
                return new MachineProxyDataImporter(importData);
            case TAIL_FILE:
                return new TailFileDataImporter(importData);
            case APP_PROFILE:
                return new AppProfileDataImporter(importData);
            case APPLICATION:
                return new ApplicationDataImporter(importData);
            case APP_REPOSITORY:
                return new AppRepositoryDataImporter(importData);
            case COMMAND_TEMPLATE:
                return new CommandTemplateDataImporter(importData);
            case WEBHOOK:
                return new WebhookDataImporter(importData);
            default:
                throw Exceptions.unsupported();
        }
    }

}
