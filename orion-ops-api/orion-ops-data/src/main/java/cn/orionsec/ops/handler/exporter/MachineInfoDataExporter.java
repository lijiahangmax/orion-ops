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
package cn.orionsec.ops.handler.exporter;

import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.kit.spring.SpringHolder;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.ExportType;
import cn.orionsec.ops.dao.MachineInfoDAO;
import cn.orionsec.ops.dao.MachineSecretKeyDAO;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.domain.MachineSecretKeyDO;
import cn.orionsec.ops.entity.exporter.MachineInfoExportDTO;
import cn.orionsec.ops.entity.request.data.DataExportRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 机器信息 数据导出器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/8 18:19
 */
public class MachineInfoDataExporter extends AbstractDataExporter<MachineInfoExportDTO> {

    private static final MachineInfoDAO machineInfoDAO = SpringHolder.getBean(MachineInfoDAO.class);

    private static final MachineSecretKeyDAO machineSecretKeyDAO = SpringHolder.getBean(MachineSecretKeyDAO.class);

    public MachineInfoDataExporter(DataExportRequest request, HttpServletResponse response) {
        super(ExportType.MACHINE_INFO, request, response);
    }

    @Override
    protected List<MachineInfoExportDTO> queryData() {
        // 查询机器信息
        List<MachineInfoDO> machineList = machineInfoDAO.selectList(null);
        List<MachineInfoExportDTO> exportList = Converts.toList(machineList, MachineInfoExportDTO.class);
        // 查询密钥
        List<Long> keyIdList = machineList.stream()
                .map(MachineInfoDO::getKeyId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (!keyIdList.isEmpty()) {
            // 查询密钥
            Map<Long, String> keyNameMap = machineSecretKeyDAO.selectBatchIds(keyIdList)
                    .stream()
                    .collect(Collectors.toMap(MachineSecretKeyDO::getId, MachineSecretKeyDO::getKeyName));
            // 设置密钥名称
            for (MachineInfoExportDTO machine : exportList) {
                machine.setKeyName(keyNameMap.get(machine.getId()));
            }
        }
        // 设置密码
        if (!Const.ENABLE.equals(request.getExportPassword())) {
            exportList.forEach(s -> s.setEncryptPassword(null));
        }
        return exportList;
    }

}
