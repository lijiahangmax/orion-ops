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
package cn.orionsec.ops.handler.importer.impl;

import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.ops.constant.ImportType;
import cn.orionsec.ops.entity.importer.DataImportDTO;

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
