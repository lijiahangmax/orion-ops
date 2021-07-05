package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.Tuple;
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
    private MachineEnvDAO machineEnvDAO;

    @Resource
    private CommandTemplateDAO commandTemplateDAO;

    @Override
    public void addHistory(Long valueId, HistoryValueType valueType, String beforeValue) {
        UserDTO user = Currents.getUser();
        HistoryValueSnapshotDO insert = new HistoryValueSnapshotDO();
        insert.setValueId(valueId);
        insert.setValueType(valueType.getType());
        insert.setBeforeValue(beforeValue);
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
        Long valueId = historyValue.getValueId();
        String updateValue = historyValue.getBeforeValue();
        HistoryValueType valueType = HistoryValueType.of(historyValue.getValueType());
        Tuple tuple;
        switch (valueType) {
            case MACHINE_ENV:
                tuple = this.rollbackMachineEnv(valueId, updateValue);
                break;
            case COMMAND_TEMPLATE:
                tuple = this.rollbackCommandTemplate(valueId, updateValue);
                break;
            default:
                return 0;
        }
        // 添加历史记录
        this.addHistory(valueId, valueType, tuple.get(1));
        return tuple.get(0);
    }

    /**
     * 回滚 机器环境变量
     *
     * @param valueId valueId
     * @param value   value
     * @return effect value
     */
    private Tuple rollbackMachineEnv(Long valueId, String value) {
        // 查询
        MachineEnvDO env = machineEnvDAO.selectById(valueId);
        Valid.notNull(env, MessageConst.METADATA_MISSING);
        // 更新
        MachineEnvDO update = new MachineEnvDO();
        update.setId(valueId);
        update.setAttrValue(value);
        update.setUpdateTime(new Date());
        Integer effect = machineEnvDAO.updateById(update);
        return Tuple.of(effect, env.getAttrValue());
    }

    /**
     * 回滚 命令模板
     *
     * @param valueId valueId
     * @param value   value
     * @return effect value
     */
    private Tuple rollbackCommandTemplate(Long valueId, String value) {
        // 查询
        CommandTemplateDO template = commandTemplateDAO.selectById(valueId);
        Valid.notNull(template, MessageConst.METADATA_MISSING);
        // 更新
        CommandTemplateDO updateTemplate = new CommandTemplateDO();
        updateTemplate.setId(valueId);
        updateTemplate.setTemplateValue(value);
        updateTemplate.setUpdateTime(new Date());
        Integer effect = commandTemplateDAO.updateById(updateTemplate);
        return Tuple.of(effect, template.getTemplateValue());
    }

}
