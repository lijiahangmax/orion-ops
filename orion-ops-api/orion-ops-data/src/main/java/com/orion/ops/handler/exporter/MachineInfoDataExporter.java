package com.orion.ops.handler.exporter;

import com.orion.lang.utils.convert.Converts;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.ExportType;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.dao.MachineSecretKeyDAO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.exporter.MachineInfoExportDTO;
import com.orion.ops.entity.request.data.DataExportRequest;
import com.orion.spring.SpringHolder;

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
        // 查询秘钥
        List<Long> keyIdList = machineList.stream()
                .map(MachineInfoDO::getKeyId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (!keyIdList.isEmpty()) {
            // 查询秘钥
            Map<Long, String> keyNameMap = machineSecretKeyDAO.selectBatchIds(keyIdList)
                    .stream()
                    .collect(Collectors.toMap(MachineSecretKeyDO::getId, MachineSecretKeyDO::getKeyName));
            // 设置秘钥名称
            for (MachineInfoExportDTO machine : exportList) {
                machine.setUsingKeyName(keyNameMap.get(machine.getId()));
            }
        }
        // 设置密码
        if (!Const.ENABLE.equals(request.getExportPassword())) {
            exportList.forEach(s -> s.setEncryptPassword(null));
        }
        return exportList;
    }

}
