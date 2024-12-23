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
import cn.orionsec.ops.dao.MachineProxyDAO;
import cn.orionsec.ops.entity.domain.MachineProxyDO;
import cn.orionsec.ops.entity.exporter.MachineProxyExportDTO;
import cn.orionsec.ops.entity.request.data.DataExportRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 机器代理 数据导出器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/8 18:22
 */
public class MachineProxyDataExporter extends AbstractDataExporter<MachineProxyExportDTO> {

    private static final MachineProxyDAO machineProxyDAO = SpringHolder.getBean(MachineProxyDAO.class);

    public MachineProxyDataExporter(DataExportRequest request, HttpServletResponse response) {
        super(ExportType.MACHINE_PROXY, request, response);
    }

    @Override
    protected List<MachineProxyExportDTO> queryData() {
        // 查询数据
        List<MachineProxyDO> proxyList = machineProxyDAO.selectList(null);
        List<MachineProxyExportDTO> exportList = Converts.toList(proxyList, MachineProxyExportDTO.class);
        if (!Const.ENABLE.equals(request.getExportPassword())) {
            exportList.forEach(s -> s.setEncryptPassword(null));
        }
        return exportList;
    }

}
