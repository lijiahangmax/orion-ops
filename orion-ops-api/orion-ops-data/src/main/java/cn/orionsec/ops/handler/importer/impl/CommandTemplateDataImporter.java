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
package cn.orionsec.ops.handler.importer.impl;

import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.dao.CommandTemplateDAO;
import cn.orionsec.ops.entity.domain.CommandTemplateDO;
import cn.orionsec.ops.entity.importer.DataImportDTO;

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
