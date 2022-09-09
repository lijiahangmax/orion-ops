package com.orion.ops.handler.importer.checker;

import com.orion.ops.constant.ImportType;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.dao.FileTailListDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.FileTailListDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.importer.MachineTailFileImportDTO;
import com.orion.ops.entity.vo.data.DataImportCheckVO;
import com.orion.spring.SpringHolder;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 日志文件 数据检查器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 16:31
 */
public class TailFileDataChecker extends AbstractDataChecker<MachineTailFileImportDTO, FileTailListDO> {

    private static final MachineInfoDAO machineInfoDAO = SpringHolder.getBean(MachineInfoDAO.class);

    private static final FileTailListDAO fileTailListDAO = SpringHolder.getBean(FileTailListDAO.class);

    public TailFileDataChecker(Workbook workbook) {
        super(ImportType.TAIL_FILE, workbook);
    }

    @Override
    protected DataImportCheckVO checkImportData(List<MachineTailFileImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(rows);
        // 设置机器id
        this.setCheckRowsRelId(rows, MachineTailFileImportDTO::getMachineTag,
                machineInfoDAO::selectIdByTagList,
                MachineInfoDO::getMachineTag,
                MachineInfoDO::getId,
                MachineTailFileImportDTO::setMachineId,
                MessageConst.UNKNOWN_MACHINE_TAG);
        // 通过别名查询文件
        List<FileTailListDO> presentFiles = this.getImportRowsPresentValues(rows, MachineTailFileImportDTO::getName,
                fileTailListDAO, FileTailListDO::getAliasName);
        // 检查数据是否存在
        this.checkImportRowsPresent(rows, MachineTailFileImportDTO::getName,
                presentFiles, FileTailListDO::getAliasName, FileTailListDO::getId);
        // 设置导入检查数据
        return this.setImportCheckRows(rows);
    }

}
