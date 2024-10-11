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
package com.orion.ops.handler.importer.checker;

import com.orion.ops.constant.ImportType;
import com.orion.ops.dao.ApplicationRepositoryDAO;
import com.orion.ops.entity.domain.ApplicationRepositoryDO;
import com.orion.ops.entity.importer.ApplicationRepositoryImportDTO;
import com.orion.ops.entity.vo.data.DataImportCheckVO;
import com.orion.spring.SpringHolder;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 应用仓库 数据检查器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 16:31
 */
public class AppRepositoryDataChecker extends AbstractDataChecker<ApplicationRepositoryImportDTO, ApplicationRepositoryDO> {

    private static final ApplicationRepositoryDAO applicationRepositoryDAO = SpringHolder.getBean(ApplicationRepositoryDAO.class);

    public AppRepositoryDataChecker(Workbook workbook) {
        super(ImportType.APP_REPOSITORY, workbook);
    }

    @Override
    protected DataImportCheckVO checkImportData(List<ApplicationRepositoryImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(rows);
        // 通过唯一标识查询应用
        List<ApplicationRepositoryDO> presentList = this.getImportRowsPresentValues(rows,
                ApplicationRepositoryImportDTO::getName,
                applicationRepositoryDAO, ApplicationRepositoryDO::getRepoName);
        // 检查数据是否存在
        this.checkImportRowsPresent(rows, ApplicationRepositoryImportDTO::getName,
                presentList, ApplicationRepositoryDO::getRepoName, ApplicationRepositoryDO::getId);
        // 设置导入检查数据
        return this.setImportCheckRows(rows);
    }

}
