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
package cn.orionsec.ops.handler.importer.checker;

import cn.orionsec.ops.constant.ImportType;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.dao.ApplicationInfoDAO;
import cn.orionsec.ops.dao.ApplicationRepositoryDAO;
import cn.orionsec.ops.entity.domain.ApplicationInfoDO;
import cn.orionsec.ops.entity.domain.ApplicationRepositoryDO;
import cn.orionsec.ops.entity.importer.ApplicationImportDTO;
import cn.orionsec.ops.entity.vo.data.DataImportCheckVO;
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
