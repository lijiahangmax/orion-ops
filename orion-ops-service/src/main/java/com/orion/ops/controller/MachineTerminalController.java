package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.Wrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.entity.request.MachineTerminalLogRequest;
import com.orion.ops.entity.request.MachineTerminalManagerRequest;
import com.orion.ops.entity.request.MachineTerminalRequest;
import com.orion.ops.entity.vo.MachineTerminalLogVO;
import com.orion.ops.entity.vo.MachineTerminalManagerVO;
import com.orion.ops.entity.vo.MachineTerminalVO;
import com.orion.ops.entity.vo.TerminalAccessVO;
import com.orion.ops.handler.terminal.manager.TerminalSessionManager;
import com.orion.ops.service.api.MachineTerminalService;
import com.orion.ops.utils.Valid;
import com.orion.remote.TerminalType;
import com.orion.utils.Strings;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 终端
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 21:45
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/terminal")
public class MachineTerminalController {

    @Resource
    private MachineTerminalService machineTerminalService;

    @Resource
    private TerminalSessionManager terminalSessionManager;

    /**
     * 获取终端 accessToken
     */
    @RequestMapping("/access")
    @EventLog(EventType.OPEN_TERMINAL)
    public TerminalAccessVO access(@RequestBody MachineTerminalRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        return machineTerminalService.getAccessConfig(machineId);
    }

    /**
     * 获取支持的终端类型
     */
    @RequestMapping("/support/pty")
    public String[] getSupportedPty() {
        return Arrays.stream(TerminalType.values())
                .map(TerminalType::getType)
                .toArray(String[]::new);
    }

    /**
     * 获取配置
     */
    @RequestMapping("/get/{machineId}")
    public MachineTerminalVO getSetting(@PathVariable Long machineId) {
        return machineTerminalService.getMachineConfig(machineId);
    }

    /**
     * 修改配置
     */
    @RequestMapping("/update")
    @EventLog(EventType.UPDATE_TERMINAL_CONFIG)
    public Integer updateSetting(@RequestBody MachineTerminalRequest request) {
        Valid.notNull(request.getId());
        String terminalType = request.getTerminalType();
        if (!Strings.isBlank(terminalType)) {
            Valid.notNull(TerminalType.of(terminalType), MessageConst.INVALID_PTY);
        }
        if (request.getEnableWebLink() != null) {
            Valid.in(request.getEnableWebLink(), Const.ENABLE, Const.DISABLE);
        }
        if (request.getEnableWebGL() != null) {
            Valid.in(request.getEnableWebGL(), Const.ENABLE, Const.DISABLE);
        }
        return machineTerminalService.updateSetting(request);
    }

    /**
     * 日志列表
     */
    @RequestMapping("/log/list")
    public DataGrid<MachineTerminalLogVO> accessLogList(@RequestBody MachineTerminalLogRequest request) {
        return machineTerminalService.listAccessLog(request);
    }

    /**
     * 删除日志
     */
    @RequestMapping("/log/delete")
    @EventLog(EventType.DELETE_TERMINATED_LOG)
    public Integer deleteLog(@RequestBody MachineTerminalLogRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return machineTerminalService.deleteTerminalLog(idList);
    }

    /**
     * session列表 (管理员)
     */
    @RequestMapping("/manager/session")
    @RequireRole(RoleType.ADMINISTRATOR)
    public DataGrid<MachineTerminalManagerVO> sessionList(@RequestBody MachineTerminalManagerRequest request) {
        return terminalSessionManager.getOnlineTerminal(request);
    }

    /**
     * 强制下线 (管理员)
     */
    @RequestMapping("/manager/offline")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.FORCE_OFFLINE_TERMINAL)
    public Wrapper<?> forceOffline(@RequestBody MachineTerminalManagerRequest request) {
        String token = Valid.notBlank(request.getToken());
        return terminalSessionManager.forceOffline(token);
    }

}
