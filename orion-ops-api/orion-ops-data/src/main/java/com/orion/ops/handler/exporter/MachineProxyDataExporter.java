package com.orion.ops.handler.exporter;

import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.ExportType;
import com.orion.ops.dao.MachineProxyDAO;
import com.orion.ops.entity.domain.MachineProxyDO;
import com.orion.ops.entity.exporter.MachineProxyExportDTO;
import com.orion.ops.entity.request.data.DataExportRequest;
import com.orion.spring.SpringHolder;

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
