package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.dao.MachineTerminalDAO;
import com.orion.ops.entity.domain.MachineTerminalDO;
import com.orion.ops.service.api.TerminalService;
import com.orion.remote.TerminalType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 终端service
 *
 * @author ljh15
 * @version 1.0.0
 * @since 2021/3/31 17:20
 */
@Service("terminalService")
public class TerminalServiceImpl implements TerminalService {

    @Resource
    private MachineTerminalDAO machineTerminalDAO;

    @Override
    public MachineTerminalDO getConfig(Long machineId) {
        MachineTerminalDO config = machineTerminalDAO.selectOne(new LambdaQueryWrapper<MachineTerminalDO>().eq(MachineTerminalDO::getMachineId, machineId));
        if (config != null) {
            return config;
        }
        MachineTerminalDO insert = new MachineTerminalDO();
        insert.setMachineId(machineId);
        insert.setTerminalType(TerminalType.XTERM_256_COLOR.getType());
        insert.setBackgroundColor(BACKGROUND_COLOR);
        insert.setFontColor(FONT_COLOR);
        insert.setFontSize(FONT_SIZE);
        machineTerminalDAO.insert(insert);
        return insert;
    }

    void b() {

    }

}
