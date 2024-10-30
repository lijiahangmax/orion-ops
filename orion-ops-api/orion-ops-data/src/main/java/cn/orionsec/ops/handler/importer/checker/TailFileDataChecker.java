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
package cn.orionsec.ops.handler.importer.checker;

import cn.orionsec.ops.constant.ImportType;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.dao.FileTailListDAO;
import cn.orionsec.ops.dao.MachineInfoDAO;
import cn.orionsec.ops.entity.domain.FileTailListDO;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.importer.MachineTailFileImportDTO;
import cn.orionsec.ops.entity.vo.data.DataImportCheckVO;
import com.orion.spring.SpringHolder;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * 日志文件 数据检查器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 16:31
 */
public class TailFileDataChecker extends AbstractDataChecker<MachineTailFileImportDTO, FileTailListDO> {

    private static final MachineInfoDAO machineInfoDAO = SpringHolder.getBean(MachineInfoDAO.class);

    private static final FileTailListDAO fileTailListDAO = SpringHolder.getBean(FileTailListDAO.class);

    public TailFileDataChecker(Workbook workbook) {
        super(ImportType.TAIL_FILE, workbook);
    }

    @Override
    protected DataImportCheckVO checkImportData(List<MachineTailFileImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(rows);
        // 设置机器id
        this.setCheckRowsRelId(rows, MachineTailFileImportDTO::getMachineTag,
                machineInfoDAO::selectIdByTagList,
                MachineInfoDO::getMachineTag,
                MachineInfoDO::getId,
                MachineTailFileImportDTO::setMachineId,
                MessageConst.UNKNOWN_MACHINE_TAG);
        // 通过别名查询文件
        List<FileTailListDO> presentFiles = this.getImportRowsPresentValues(rows, MachineTailFileImportDTO::getName,
                fileTailListDAO, FileTailListDO::getAliasName);
        // 检查数据是否存在
        this.checkImportRowsPresent(rows, MachineTailFileImportDTO::getName,
                presentFiles, FileTailListDO::getAliasName, FileTailListDO::getId);
        // 设置导入检查数据
        return this.setImportCheckRows(rows);
    }

}
