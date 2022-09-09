package com.orion.ops.handler.importer.impl;

import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.importer.DataImportDTO;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.spring.SpringHolder;

import java.util.Optional;

/**
 * 机器信息 数据导入器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 17:14
 */
public class MachineInfoDataImporter extends AbstractDataImporter<MachineInfoDO> {

    private static final MachineInfoDAO machineInfoDAO = SpringHolder.getBean(MachineInfoDAO.class);

    private static final MachineEnvService machineEnvService = SpringHolder.getBean(MachineEnvService.class);

    public MachineInfoDataImporter(DataImportDTO importData) {
        super(importData, machineInfoDAO);
    }

    @Override
    protected void updateFiller(MachineInfoDO row) {
        // 检查是否有代理
        Optional.ofNullable(row.getId())
                .map(machineInfoDAO::selectById)
                .map(MachineInfoDO::getProxyId)
                .ifPresent(row::setProxyId);
    }

    @Override
    protected void insertCallback(MachineInfoDO row) {
        // 初始化新增机器环境变量
        machineEnvService.initEnv(row.getId());
    }

}
