package com.orion.ops.handler.importer.checker;

import com.orion.lang.utils.Exceptions;
import com.orion.ops.constant.ImportType;
import com.orion.ops.entity.vo.data.DataImportCheckVO;
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
