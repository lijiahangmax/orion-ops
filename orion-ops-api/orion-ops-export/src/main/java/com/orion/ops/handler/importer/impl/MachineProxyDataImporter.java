package com.orion.ops.handler.importer.impl;

import com.orion.ops.dao.MachineProxyDAO;
import com.orion.ops.entity.domain.MachineProxyDO;
import com.orion.ops.entity.importer.DataImportDTO;
import com.orion.spring.SpringHolder;

/**
 * 机器代理 数据导入器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 17:18
 */
public class MachineProxyDataImporter extends AbstractDataImporter<MachineProxyDO> {

    private static final MachineProxyDAO machineProxyDAO = SpringHolder.getBean(MachineProxyDAO.class);

    public MachineProxyDataImporter(DataImportDTO importData) {
        super(importData, machineProxyDAO);
    }

}
