package com.orion.ops.handler.release.action;

import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.handler.release.hint.ReleaseActionHint;
import com.orion.ops.handler.release.hint.ReleaseHint;
import com.orion.ops.handler.release.hint.ReleaseMachineHint;
import com.orion.ops.service.api.MachineInfoService;
import com.orion.ops.utils.Valid;
import com.orion.remote.channel.SessionStore;
import com.orion.spring.SpringHolder;

import java.util.List;
import java.util.Map;

/**
 * 宿主机建立连接处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.consts.app.ActionType#CONNECT
 * @since 2021/7/15 17:24
 */
public class ReleaseConnectActionHandler extends AbstractReleaseHostActionHandler {

    private static MachineInfoService machineInfoService = SpringHolder.getBean("machineInfoService");

    public ReleaseConnectActionHandler(ReleaseHint hint, ReleaseActionHint action) {
        super(hint, action);
    }

    @Override
    protected void handleAction() {
        Map<Long, SessionStore> sessionHolder = hint.getSessionHolder();
        List<ReleaseMachineHint> machines = hint.getMachines();
        // 临时插入宿主机
        ReleaseMachineHint hostMachine = new ReleaseMachineHint();
        hostMachine.setMachineId(Const.HOST_MACHINE_ID);
        machines.add(0, hostMachine);
        // 打开连接
        for (ReleaseMachineHint machine : machines) {
            // 查询机器
            Long machineId = machine.getMachineId();
            MachineInfoDO machineInfo = machineInfoService.selectById(machineId);
            Valid.notNull(machineInfo, MessageConst.RELEASE_MACHINE_ABSENT + " id: " + machineId);
            machine.setMachineInfo(machineInfo);
            this.printLog("开始建立连接 {}@{}:{}", machineInfo.getUsername(), machineInfo.getMachineHost(), machineInfo.getSshPort());
            // 打开session
            SessionStore sessionStore = machineInfoService.openSessionStore(machineInfo);
            sessionHolder.put(machineId, sessionStore);
        }
        // 移除宿主机
        machines.remove(0);
    }

}
