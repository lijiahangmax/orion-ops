package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.Wrapper;
import com.orion.ops.annotation.IgnoreWrapper;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.RoleType;
import com.orion.ops.entity.request.MachineTerminalLogRequest;
import com.orion.ops.entity.request.MachineTerminalManagerRequest;
import com.orion.ops.entity.request.MachineTerminalRequest;
import com.orion.ops.entity.vo.MachineTerminalLogVO;
import com.orion.ops.entity.vo.MachineTerminalManagerVO;
import com.orion.ops.entity.vo.TerminalAccessVO;
import com.orion.ops.handler.terminal.manager.TerminalSessionManager;
import com.orion.ops.service.api.MachineTerminalService;
import com.orion.ops.utils.Valid;
import com.orion.remote.TerminalType;
import com.orion.servlet.web.Servlets;
import com.orion.utils.Strings;
import com.orion.utils.io.FileReaders;
import com.orion.utils.io.Files1;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

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
     * 获取accessToken
     */
    @RequestMapping("/access")
    public TerminalAccessVO access(@RequestBody MachineTerminalRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        return machineTerminalService.getAccessConfig(machineId);
    }

    /**
     * 修改配置
     */
    @RequestMapping("/setting")
    public Integer setting(@RequestBody MachineTerminalRequest request) {
        Valid.notNull(request.getId());
        String terminalType = request.getTerminalType();
        if (!Strings.isBlank(terminalType)) {
            Valid.notNull(TerminalType.of(terminalType), "终端类型不合法");
        }
        return machineTerminalService.setting(request);
    }

    /**
     * 日志列表
     */
    @RequestMapping("/log/list")
    public DataGrid<MachineTerminalLogVO> accessLogList(@RequestBody MachineTerminalLogRequest request) {
        return machineTerminalService.listAccessLog(request);
    }

    /**
     * 检查日志是否存在
     */
    @RequestMapping("/log/check")
    public Integer checkLogExist(@RequestBody MachineTerminalLogRequest request) {
        Long id = Valid.notNull(request.getId());
        return machineTerminalService.checkLogExist(id);
    }

    // 只能自己看
    // 前缀
    // 重连
    /**
     * 下载日志文件
     */
    @RequestMapping("/log/download/{id}")
    @IgnoreWrapper
    public void downloadLogFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Valid.notNull(id);
        String filePath = machineTerminalService.getLogFilePath(id);
        if (filePath == null) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=notfound.log");
            return;
        }
        File file = new File(filePath);
        if (!Files1.isFile(file)) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=notfound.log");
            return;
        }
        Servlets.transfer(response, FileReaders.readFast(file), Files1.getFileName(file));
    }

    /**
     * 强制下线 (管理员)
     */
    @RequestMapping("/manager/offline")
    @RequireRole(value = {RoleType.ADMINISTRATOR, RoleType.ADMINISTRATOR})
    public Wrapper<?> forceOffline(@RequestBody MachineTerminalManagerRequest request) {
        String token = Valid.notBlank(request.getToken());
        return terminalSessionManager.forceOffline(token);
    }

    /**
     * session列表 (管理员)
     */
    @RequestMapping("/manager/session/list")
    @RequireRole(value = {RoleType.ADMINISTRATOR, RoleType.ADMINISTRATOR})
    public DataGrid<MachineTerminalManagerVO> sessionList(@RequestBody MachineTerminalManagerRequest request) {
        return terminalSessionManager.getOnlineTerminal(request);
    }

}
