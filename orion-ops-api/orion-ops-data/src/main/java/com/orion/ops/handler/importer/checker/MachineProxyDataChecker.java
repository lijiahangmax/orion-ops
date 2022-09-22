package com.orion.ops.handler.importer.checker;

import com.orion.ops.constant.ImportType;
import com.orion.ops.entity.domain.MachineProxyDO;
import com.orion.ops.entity.importer.MachineProxyImportDTO;
import com.orion.ops.entity.vo.data.DataImportCheckVO;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 机器代理 数据检查器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 16:29
 */
public class MachineProxyDataChecker extends AbstractDataChecker<MachineProxyImportDTO, MachineProxyDO> {

    public MachineProxyDataChecker(Workbook workbook) {
        super(ImportType.MACHINE_PROXY, workbook);
    }

    @Override
    protected DataImportCheckVO checkImportData(List<MachineProxyImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(rows);
        // 设置导入检查数据
        return this.setImportCheckRows(rows);
    }

}
