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
package com.orion.ops.handler.importer.checker;

import com.orion.ops.constant.ImportType;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.dao.MachineSecretKeyDAO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.importer.MachineInfoImportDTO;
import com.orion.ops.entity.vo.data.DataImportCheckVO;
import com.orion.spring.SpringHolder;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 机器信息 数据检查器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 16:06
 */
public class MachineInfoDataChecker extends AbstractDataChecker<MachineInfoImportDTO, MachineInfoDO> {

    private static final MachineInfoDAO machineInfoDAO = SpringHolder.getBean(MachineInfoDAO.class);

    private static final MachineSecretKeyDAO machineSecretKeyDAO = SpringHolder.getBean(MachineSecretKeyDAO.class);

    public MachineInfoDataChecker(Workbook workbook) {
        super(ImportType.MACHINE_INFO, workbook);
    }

    @Override
    protected DataImportCheckVO checkImportData(List<MachineInfoImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(rows);
        // 设置机器id
        this.setCheckRowsRelId(rows, MachineInfoImportDTO::getKeyName,
                machineSecretKeyDAO::selectIdByNameList,
                MachineSecretKeyDO::getKeyName,
                MachineSecretKeyDO::getId,
                MachineInfoImportDTO::setKeyId,
                MessageConst.UNKNOWN_MACHINE_KEY);
        // 通过唯一标识查询机器
        List<MachineInfoDO> presentMachines = this.getImportRowsPresentValues(rows, MachineInfoImportDTO::getTag,
                machineInfoDAO, MachineInfoDO::getMachineTag);
        // 检查数据是否存在
        this.checkImportRowsPresent(rows, MachineInfoImportDTO::getTag,
                presentMachines, MachineInfoDO::getMachineTag, MachineInfoDO::getId);
        // 设置导入检查数据
        return this.setImportCheckRows(rows);
    }

}
