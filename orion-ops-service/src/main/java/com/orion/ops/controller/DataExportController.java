package com.orion.ops.controller;

import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.entity.request.DataExportRequest;
import com.orion.ops.service.api.DataExportService;
import com.orion.ops.utils.Currents;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 数据导出 controller
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 14:02
 */
@RestController
@RequestMapping("/orion/api/data-export")
public class DataExportController {

    @Resource
    private DataExportService dataExportService;

    /**
     * 导出机器
     */
    @RequestMapping("/machine")
    @EventLog(EventType.DATA_EXPORT_MACHINE)
    public void exportMachine(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportMachine(request, response);
    }

    /**
     * 导出机器代理
     */
    @RequestMapping("/machine-proxy")
    @EventLog(EventType.DATA_EXPORT_MACHINE_PROXY)
    public void exportMachineProxy(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportMachineProxy(request, response);
    }

    /**
     * 导出终端日志
     */
    @RequestMapping("/machine-terminal-log")
    @EventLog(EventType.DATA_EXPORT_TERMINAL_LOG)
    @RequireRole(RoleType.ADMINISTRATOR)
    public void exportMachineTerminalLog(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportMachineTerminalLog(request, response);
    }

    /**
     * 导出日志文件
     */
    @RequestMapping("/machine-tail-file")
    @EventLog(EventType.DATA_EXPORT_TAIL_FILE)
    public void exportMachineTailFile(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportMachineTailFile(request, response);
    }

    /**
     * 导出应用环境
     */
    @RequestMapping("/app-profile")
    @EventLog(EventType.DATA_EXPORT_APP_PROFILE)
    public void exportAppProfile(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportAppProfile(request, response);
    }

    /**
     * 导出应用信息
     */
    @RequestMapping("/application")
    @EventLog(EventType.DATA_EXPORT_APPLICATION)
    public void exportApplication(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportApplication(request, response);
    }

    /**
     * 导出应用版本仓库
     */
    @RequestMapping("/app-vcs")
    @EventLog(EventType.DATA_EXPORT_APP_VCS)
    public void exportAppVcs(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportAppVcs(request, response);
    }

    /**
     * 导出命令模板
     */
    @RequestMapping("/command-template")
    @EventLog(EventType.DATA_EXPORT_COMMAND_TEMPLATE)
    public void exportCommandTemplate(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportCommandTemplate(request, response);
    }

    /**
     * 导出站内信
     */
    @RequestMapping("/web-side-message")
    @EventLog(EventType.DATA_EXPORT_WEB_SIDE_MESSAGE)
    public void exportWebSideMessage(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportWebSideMessage(request, response);
    }

    /**
     * 导出操作日志
     */
    @RequestMapping("/event-log")
    @EventLog(EventType.DATA_EXPORT_EVENT_LOG)
    public void exportEventLog(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        if (Currents.isAdministrator()) {
            if (Const.ENABLE.equals(request.getOnlyMyself())) {
                request.setUserId(Currents.getUserId());
            }
        } else {
            request.setUserId(Currents.getUserId());
        }
        dataExportService.exportEventLog(request, response);
    }

}
