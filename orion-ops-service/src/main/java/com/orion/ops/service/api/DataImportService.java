package com.orion.ops.service.api;

import com.orion.ops.constant.export.ImportType;
import com.orion.ops.entity.dto.importer.*;
import com.orion.ops.entity.vo.DataImportCheckVO;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 数据导入服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 17:06
 */
public interface DataImportService {

    /**
     * 解析导入 workbook
     *
     * @param workbook workbook
     * @param type     type
     * @param <T>      T
     * @return list
     */
    <T> List<T> parseImportWorkbook(ImportType type, Workbook workbook);

    /**
     * 检查机器导入信息
     *
     * @param rows rows
     * @return 导入信息
     */
    DataImportCheckVO checkMachineInfoImportData(List<MachineInfoImportDTO> rows);

    /**
     * 检查机器代理导入信息
     *
     * @param rows rows
     * @return 导入信息
     */
    DataImportCheckVO checkMachineProxyImportData(List<MachineProxyImportDTO> rows);

    /**
     * 检查日志文件导入信息
     *
     * @param rows rows
     * @return 导入信息
     */
    DataImportCheckVO checkMachineTailFileImportData(List<MachineTailFileImportDTO> rows);

    /**
     * 检查应用环境导入信息
     *
     * @param rows rows
     * @return 导入信息
     */
    DataImportCheckVO checkAppProfileImportData(List<ApplicationProfileImportDTO> rows);

    /**
     * 检查应用信息导入信息
     *
     * @param rows rows
     * @return 导入信息
     */
    DataImportCheckVO checkApplicationInfoImportData(List<ApplicationImportDTO> rows);

    /**
     * 检查应用版本仓库导入信息
     *
     * @param rows rows
     * @return 导入信息
     */
    DataImportCheckVO checkAppVcsImportData(List<ApplicationRepositoryImportDTO> rows);

    /**
     * 检查命令模板导入信息
     *
     * @param rows rows
     * @return 导入信息
     */
    DataImportCheckVO checkCommandTemplateImportData(List<CommandTemplateImportDTO> rows);

    /**
     * 导入机器信息
     *
     * @param checkData 缓存信息
     */
    void importMachineInfoData(DataImportDTO checkData);

    /**
     * 导入机器代理
     *
     * @param checkData checkData
     */
    void importMachineProxyData(DataImportDTO checkData);

    /**
     * 导入日志文件
     *
     * @param checkData checkData
     */
    void importMachineTailFileData(DataImportDTO checkData);

    /**
     * 导入应用环境
     *
     * @param checkData checkData
     */
    void importAppProfileData(DataImportDTO checkData);

    /**
     * 导入应用信息
     *
     * @param checkData checkData
     */
    void importApplicationData(DataImportDTO checkData);

    /**
     * 导入应用版本仓库
     *
     * @param checkData checkData
     */
    void importAppVcsData(DataImportDTO checkData);

    /**
     * 导入命令模板
     *
     * @param checkData checkData
     */
    void importCommandTemplateData(DataImportDTO checkData);

    /**
     * 检查导入 token
     *
     * @param token token
     * @return 导入数据
     */
    DataImportDTO checkImportToken(String token);

    /**
     * 清空导入 token
     *
     * @param token token
     */
    void clearImportToken(String token);

}
