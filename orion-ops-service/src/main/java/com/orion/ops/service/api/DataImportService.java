package com.orion.ops.service.api;

import com.orion.ops.consts.export.ImportType;
import com.orion.ops.entity.dto.importer.DataImportDTO;
import com.orion.ops.entity.dto.importer.MachineInfoImportDTO;
import com.orion.ops.entity.dto.importer.MachineProxyImportDTO;
import com.orion.ops.entity.dto.importer.MachineTailFileImportDTO;
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
