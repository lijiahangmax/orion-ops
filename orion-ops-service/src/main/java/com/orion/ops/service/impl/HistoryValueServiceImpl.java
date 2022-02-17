package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.history.HistoryOperator;
import com.orion.ops.consts.history.HistoryValueType;
import com.orion.ops.dao.HistoryValueSnapshotDAO;
import com.orion.ops.entity.domain.HistoryValueSnapshotDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.ApplicationEnvRequest;
import com.orion.ops.entity.request.HistoryValueRequest;
import com.orion.ops.entity.request.MachineEnvRequest;
import com.orion.ops.entity.request.SystemEnvRequest;
import com.orion.ops.entity.vo.HistoryValueVO;
import com.orion.ops.service.api.ApplicationEnvService;
import com.orion.ops.service.api.HistoryValueService;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.service.api.SystemEnvService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 历史值快照 实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 17:19
 */
@Service("historyValueService")
public class HistoryValueServiceImpl implements HistoryValueService {

    @Resource
    private HistoryValueSnapshotDAO historyValueSnapshotDAO;

    @Resource
    private MachineEnvService machineEnvService;

    @Resource
    private ApplicationEnvService applicationEnvService;

    @Resource
    private SystemEnvService systemEnvService;

    @Override
    public void addHistory(Long valueId, HistoryValueType valueType, HistoryOperator operatorType, String beforeValue, String afterValue) {
        UserDTO user = Currents.getUser();
        HistoryValueSnapshotDO insert = new HistoryValueSnapshotDO();
        insert.setValueId(valueId);
        insert.setOperatorType(operatorType.getType());
        insert.setValueType(valueType.getType());
        insert.setBeforeValue(beforeValue);
        insert.setAfterValue(afterValue);
        if (user != null) {
            insert.setUpdateUserId(user.getId());
            insert.setUpdateUserName(user.getUsername());
        }
        insert.setCreateTime(new Date());
        insert.setUpdateTime(new Date());
        historyValueSnapshotDAO.insert(insert);
    }

    @Override
    public DataGrid<HistoryValueVO> list(HistoryValueRequest request) {
        LambdaQueryWrapper<HistoryValueSnapshotDO> wrapper = new LambdaQueryWrapper<HistoryValueSnapshotDO>()
                .eq(HistoryValueSnapshotDO::getValueId, request.getValueId())
                .eq(HistoryValueSnapshotDO::getValueType, request.getValueType())
                .orderByDesc(HistoryValueSnapshotDO::getId);
        return DataQuery.of(historyValueSnapshotDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(HistoryValueVO.class);
    }

    @Override
    public void rollback(Long id) {
        HistoryValueSnapshotDO historyValue = historyValueSnapshotDAO.selectById(id);
        Valid.notNull(historyValue, MessageConst.HISTORY_VALUE_ABSENT);
        // 设置修改值
        HistoryOperator operator = HistoryOperator.of(historyValue.getOperatorType());
        String updateValue;
        switch (operator) {
            case ADD:
                updateValue = historyValue.getAfterValue();
                break;
            case UPDATE:
            case DELETE:
            default:
                updateValue = historyValue.getBeforeValue();
        }
        // 修改值
        Long valueId = historyValue.getValueId();
        HistoryValueType valueType = HistoryValueType.of(historyValue.getValueType());
        switch (valueType) {
            case MACHINE_ENV:
                MachineEnvRequest machineEnvRequest = new MachineEnvRequest();
                machineEnvRequest.setId(valueId);
                machineEnvRequest.setValue(updateValue);
                machineEnvService.updateEnv(machineEnvRequest);
                return;
            case APP_ENV:
                ApplicationEnvRequest appEnvRequest = new ApplicationEnvRequest();
                appEnvRequest.setId(valueId);
                appEnvRequest.setValue(updateValue);
                applicationEnvService.updateAppEnv(appEnvRequest);
                return;
            case SYSTEM_ENV:
                SystemEnvRequest systemRequest = new SystemEnvRequest();
                systemRequest.setId(valueId);
                systemRequest.setValue(updateValue);
                systemEnvService.updateEnv(systemRequest);
                return;
            default:
        }
    }

}
