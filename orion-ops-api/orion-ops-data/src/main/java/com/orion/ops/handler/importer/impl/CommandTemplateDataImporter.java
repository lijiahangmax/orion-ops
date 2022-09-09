package com.orion.ops.handler.importer.impl;

import com.orion.ops.dao.CommandTemplateDAO;
import com.orion.ops.entity.domain.CommandTemplateDO;
import com.orion.ops.entity.importer.DataImportDTO;
import com.orion.spring.SpringHolder;

/**
 * 命令模板 数据导入器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 17:28
 */
public class CommandTemplateDataImporter extends AbstractDataImporter<CommandTemplateDO> {

    private static final CommandTemplateDAO commandTemplateDAO = SpringHolder.getBean(CommandTemplateDAO.class);

    public CommandTemplateDataImporter(DataImportDTO importData) {
        super(importData, commandTemplateDAO);
    }

    @Override
    protected void insertFiller(CommandTemplateDO row) {
        row.setCreateUserId(importData.getUserId());
        row.setCreateUserName(importData.getUserName());
        row.setUpdateUserId(importData.getUserId());
        row.setUpdateUserName(importData.getUserName());
    }

    @Override
    protected void updateFiller(CommandTemplateDO row) {
        row.setUpdateUserId(importData.getUserId());
        row.setUpdateUserName(importData.getUserName());
    }

}
