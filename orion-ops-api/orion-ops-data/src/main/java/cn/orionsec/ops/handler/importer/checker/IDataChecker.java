/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
package cn.orionsec.ops.handler.importer.checker;

import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.ops.constant.ImportType;
import cn.orionsec.ops.entity.vo.data.DataImportCheckVO;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * 导入数据检查器 接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 15:56
 */
public interface IDataChecker {

    /**
     * 检查数据
     *
     * @return check info
     */
    DataImportCheckVO doCheck();

    /**
     * 创建数据检查器
     *
     * @param importType importType
     * @param workbook   workbook
     * @return checker
     */
    static IDataChecker create(ImportType importType, Workbook workbook) {
        switch (importType) {
            case MACHINE_INFO:
                return new MachineInfoDataChecker(workbook);
            case MACHINE_PROXY:
                return new MachineProxyDataChecker(workbook);
            case TAIL_FILE:
                return new TailFileDataChecker(workbook);
            case APP_PROFILE:
                return new AppProfileDataChecker(workbook);
            case APPLICATION:
                return new ApplicationDataChecker(workbook);
            case APP_REPOSITORY:
                return new AppRepositoryDataChecker(workbook);
            case COMMAND_TEMPLATE:
                return new CommandTemplateDataChecker(workbook);
            case WEBHOOK:
                return new WebhookDataChecker(workbook);
            default:
                throw Exceptions.unsupported();
        }
    }

}
