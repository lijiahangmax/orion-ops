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

import cn.orionsec.ops.dao.MachineInfoDAO;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.importer.DataImportDTO;
import cn.orionsec.ops.service.api.MachineEnvService;
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
        // 填充忽略的字段
        Optional.ofNullable(row.getId())
                .map(machineInfoDAO::selectById)
                .ifPresent(m -> {
                    row.setProxyId(m.getProxyId());
                    row.setKeyId(m.getKeyId());
                });
    }

    @Override
    protected void insertCallback(MachineInfoDO row) {
        // 初始化新增机器环境变量
        machineEnvService.initEnv(row.getId());
    }

}
