package com.orion.ops.handler.importer.impl;

import com.orion.ops.dao.FileTailListDAO;
import com.orion.ops.entity.domain.FileTailListDO;
import com.orion.ops.entity.importer.DataImportDTO;
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
