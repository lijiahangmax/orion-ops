package com.orion.ops.handler.importer.checker;

import com.orion.ops.constant.ImportType;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.dao.ApplicationInfoDAO;
import com.orion.ops.dao.ApplicationRepositoryDAO;
import com.orion.ops.entity.domain.ApplicationInfoDO;
import com.orion.ops.entity.domain.ApplicationRepositoryDO;
import com.orion.ops.entity.importer.ApplicationImportDTO;
import com.orion.ops.entity.vo.data.DataImportCheckVO;
import com.orion.spring.SpringHolder;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 应用信息 数据检查器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 16:31
 */
public class ApplicationDataChecker extends AbstractDataChecker<ApplicationImportDTO, ApplicationInfoDO> {

    private static final ApplicationInfoDAO applicationInfoDAO = SpringHolder.getBean(ApplicationInfoDAO.class);

    private static final ApplicationRepositoryDAO applicationRepositoryDAO = SpringHolder.getBean(ApplicationRepositoryDAO.class);

    public ApplicationDataChecker(Workbook workbook) {
        super(ImportType.APPLICATION, workbook);
    }

    @Override
    protected DataImportCheckVO checkImportData(List<ApplicationImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(rows);
        // 设置机器id
        this.setCheckRowsRelId(rows, ApplicationImportDTO::getRepositoryName,
                applicationRepositoryDAO::selectIdByNameList,
                ApplicationRepositoryDO::getRepoName,
                ApplicationRepositoryDO::getId,
                ApplicationImportDTO::setRepositoryId,
                MessageConst.UNKNOWN_APP_REPOSITORY);
        // 通过唯一标识查询应用
        List<ApplicationInfoDO> presentApps = this.getImportRowsPresentValues(rows,
                ApplicationImportDTO::getTag,
                applicationInfoDAO, ApplicationInfoDO::getAppTag);
        // 检查数据是否存在
        this.checkImportRowsPresent(rows, ApplicationImportDTO::getTag,
                presentApps, ApplicationInfoDO::getAppTag, ApplicationInfoDO::getId);
        // 设置导入检查数据
        return this.setImportCheckRows(rows);
    }

}
