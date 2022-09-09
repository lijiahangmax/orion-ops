package com.orion.ops.handler.importer.checker;

import com.orion.ops.constant.ImportType;
import com.orion.ops.dao.ApplicationProfileDAO;
import com.orion.ops.entity.domain.ApplicationProfileDO;
import com.orion.ops.entity.importer.ApplicationProfileImportDTO;
import com.orion.ops.entity.vo.data.DataImportCheckVO;
import com.orion.spring.SpringHolder;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 应用环境 数据检查器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 16:35
 */
public class AppProfileDataChecker extends AbstractDataChecker<ApplicationProfileImportDTO, ApplicationProfileDO> {

    private static final ApplicationProfileDAO applicationProfileDAO = SpringHolder.getBean(ApplicationProfileDAO.class);

    public AppProfileDataChecker(Workbook workbook) {
        super(ImportType.APP_PROFILE, workbook);
    }

    @Override
    protected DataImportCheckVO checkImportData(List<ApplicationProfileImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(rows);
        // 通过唯一标识查询环境
        List<ApplicationProfileDO> presentProfiles = this.getImportRowsPresentValues(rows,
                ApplicationProfileImportDTO::getTag,
                applicationProfileDAO, ApplicationProfileDO::getProfileTag);
        // 检查数据是否存在
        this.checkImportRowsPresent(rows, ApplicationProfileImportDTO::getTag,
                presentProfiles, ApplicationProfileDO::getProfileTag, ApplicationProfileDO::getId);
        // 设置导入检查数据
        return this.setImportCheckRows(rows);
    }

}
