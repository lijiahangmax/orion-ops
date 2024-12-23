/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
import cn.orionsec.ops.dao.ApplicationInfoDAO;
import cn.orionsec.ops.entity.domain.ApplicationInfoDO;
import cn.orionsec.ops.entity.importer.DataImportDTO;
import cn.orionsec.ops.service.api.ApplicationInfoService;

/**
 * 应用信息 数据导入器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 17:24
 */
public class ApplicationDataImporter extends AbstractDataImporter<ApplicationInfoDO> {

    private static final ApplicationInfoDAO applicationInfoDAO = SpringHolder.getBean(ApplicationInfoDAO.class);

    private static final ApplicationInfoService applicationInfoService = SpringHolder.getBean(ApplicationInfoService.class);

    public ApplicationDataImporter(DataImportDTO importData) {
        super(importData, applicationInfoDAO);
    }

    @Override
    protected void insertFiller(ApplicationInfoDO row) {
        row.setAppSort(applicationInfoService.getNextSort());
    }

}
