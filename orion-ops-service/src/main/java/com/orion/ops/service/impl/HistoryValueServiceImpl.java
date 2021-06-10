package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.HistoryValueType;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.dao.CommandTemplateDAO;
import com.orion.ops.dao.HistoryValueSnapshotDAO;
import com.orion.ops.dao.MachineEnvDAO;
import com.orion.ops.entity.domain.CommandTemplateDO;
import com.orion.ops.entity.domain.HistoryValueSnapshotDO;
import com.orion.ops.entity.domain.MachineEnvDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.HistoryValueRequest;
import com.orion.ops.entity.vo.HistoryValueVO;
import com.orion.ops.service.api.HistoryValueService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    private CommandTemplateDAO commandTemplateDAO;

    @Resource
    private MachineEnvDAO machineEnvDAO;

    @Override
    public void addHistory(Long valueId, HistoryValueType valueType, String afterValue) {
        UserDTO user = Currents.getUser();
        HistoryValueSnapshotDO insert = new HistoryValueSnapshotDO();
        insert.setValueId(valueId);
        insert.setValueType(valueType.getType());
        insert.setAfterValue(afterValue);
        insert.setUpdateUserId(user.getId());
        insert.setUpdateUserName(user.getUsername());
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
    public Integer rollback(Long id) {
        HistoryValueSnapshotDO historyValue = historyValueSnapshotDAO.selectById(id);
        Valid.notNull(historyValue, MessageConst.HISTORY_VALUE_MISSING);
        // 修改值
        int effect;
        Long valueId = historyValue.getValueId();
        String afterValue = historyValue.getAfterValue();
        HistoryValueType valueType = HistoryValueType.of(historyValue.getValueType());
        switch (valueType) {
            case MACHINE_ENV:
                MachineEnvDO updateMachine = new MachineEnvDO();
                updateMachine.setId(valueId);
                updateMachine.setAttrValue(afterValue);
                effect = machineEnvDAO.updateById(updateMachine);
                break;
            case COMMAND_TEMPLATE:
                CommandTemplateDO updateTemplate = new CommandTemplateDO();
                updateTemplate.setId(valueId);
                updateTemplate.setTemplateValue(afterValue);
                effect = commandTemplateDAO.updateById(updateTemplate);
                break;
            default:
                return 0;
        }
        // 添加历史记录
        this.addHistory(valueId, valueType, afterValue);
        return effect;
    }

}
