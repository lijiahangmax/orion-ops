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

import cn.orionsec.ops.dao.ApplicationProfileDAO;
import cn.orionsec.ops.entity.domain.ApplicationProfileDO;
import cn.orionsec.ops.entity.importer.DataImportDTO;
import cn.orionsec.ops.service.api.ApplicationProfileService;
import com.orion.spring.SpringHolder;

/**
 * 应用环境 数据导入器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 17:22
 */
public class AppProfileDataImporter extends AbstractDataImporter<ApplicationProfileDO> {

    private static final ApplicationProfileDAO applicationProfileDAO = SpringHolder.getBean(ApplicationProfileDAO.class);

    private static final ApplicationProfileService applicationProfileService = SpringHolder.getBean(ApplicationProfileService.class);

    public AppProfileDataImporter(DataImportDTO importData) {
        super(importData, applicationProfileDAO);
    }

    @Override
    protected void importFinishCallback(boolean isSuccess) {
        applicationProfileService.clearProfileCache();
    }

}
