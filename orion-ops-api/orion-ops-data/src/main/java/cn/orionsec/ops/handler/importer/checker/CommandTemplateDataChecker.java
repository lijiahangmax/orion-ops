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
import cn.orionsec.ops.dao.CommandTemplateDAO;
import cn.orionsec.ops.entity.domain.CommandTemplateDO;
import cn.orionsec.ops.entity.importer.CommandTemplateImportDTO;
import cn.orionsec.ops.entity.vo.data.DataImportCheckVO;
import com.orion.spring.SpringHolder;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 命令模板 数据检查器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 16:35
 */
public class CommandTemplateDataChecker extends AbstractDataChecker<CommandTemplateImportDTO, CommandTemplateDO> {

    private static final CommandTemplateDAO commandTemplateDAO = SpringHolder.getBean(CommandTemplateDAO.class);

    public CommandTemplateDataChecker(Workbook workbook) {
        super(ImportType.COMMAND_TEMPLATE, workbook);
    }

    @Override
    protected DataImportCheckVO checkImportData(List<CommandTemplateImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(rows);
        // 通过名称查询模板
        List<CommandTemplateDO> presentTemplates = this.getImportRowsPresentValues(rows,
                CommandTemplateImportDTO::getName,
                commandTemplateDAO, CommandTemplateDO::getTemplateName);
        // 检查数据是否存在
        this.checkImportRowsPresent(rows, CommandTemplateImportDTO::getName,
                presentTemplates, CommandTemplateDO::getTemplateName, CommandTemplateDO::getId);
        // 设置导入检查数据
        return this.setImportCheckRows(rows);
    }

}
