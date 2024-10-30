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

import cn.orionsec.ops.dao.FileTailListDAO;
import cn.orionsec.ops.entity.domain.FileTailListDO;
import cn.orionsec.ops.entity.importer.DataImportDTO;
import com.orion.spring.SpringHolder;

/**
 * 日志文件 数据导入器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 17:19
 */
public class TailFileDataImporter extends AbstractDataImporter<FileTailListDO> {

    private static final FileTailListDAO fileTailListDAO = SpringHolder.getBean(FileTailListDAO.class);

    public TailFileDataImporter(DataImportDTO importData) {
        super(importData, fileTailListDAO);
    }

}
