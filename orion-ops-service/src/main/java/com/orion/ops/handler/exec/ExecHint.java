package com.orion.ops.handler.exec;

import com.orion.ops.consts.command.ExecType;
import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import lombok.Data;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 23:05
 */
@Data
public class ExecHint {

    /**
     * 执行类型
     */
    private ExecType execType;

    /**
     * 执行信息
     */
    private CommandExecDO record;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器信息
     */
    private MachineInfoDO machine;

}
