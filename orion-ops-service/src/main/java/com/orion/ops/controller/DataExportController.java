package com.orion.ops.controller;

import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.entity.request.DataExportRequest;
import com.orion.ops.service.api.DataExportService;
import com.orion.ops.utils.Currents;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 数据导出 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 14:02
 */
@Api(tags = "数据导出")
@RestController
@RequestMapping("/orion/api/data-export")
public class DataExportController {

    @Resource
    private DataExportService dataExportService;

    @PostMapping("/machine")
    @ApiOperation(value = "导出机器信息")
    @EventLog(EventType.DATA_EXPORT_MACHINE)
    public void exportMachine(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportMachine(request, response);
    }

    @PostMapping("/machine-proxy")
    @ApiOperation(value = "导出机器代理")
    @EventLog(EventType.DATA_EXPORT_MACHINE_PROXY)
    public void exportMachineProxy(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportMachineProxy(request, response);
    }

    @PostMapping("/machine-terminal-log")
    @ApiOperation(value = "导出终端日志")
    @EventLog(EventType.DATA_EXPORT_TERMINAL_LOG)
    @RequireRole(RoleType.ADMINISTRATOR)
    public void exportMachineTerminalLog(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportMachineTerminalLog(request, response);
    }

    @PostMapping("/machine-tail-file")
    @ApiOperation(value = "导出日志文件")
    @EventLog(EventType.DATA_EXPORT_TAIL_FILE)
    public void exportMachineTailFile(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportMachineTailFile(request, response);
    }

    @PostMapping("/app-profile")
    @ApiOperation(value = "导出应用环境")
    @EventLog(EventType.DATA_EXPORT_APP_PROFILE)
    public void exportAppProfile(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportAppProfile(request, response);
    }

    @PostMapping("/application")
    @ApiOperation(value = "导出应用信息")
    @EventLog(EventType.DATA_EXPORT_APPLICATION)
    public void exportApplication(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportApplication(request, response);
    }

    @PostMapping("/app-vcs")
    @ApiOperation(value = "导出应用版本仓库")
    @EventLog(EventType.DATA_EXPORT_APP_VCS)
    public void exportAppVcs(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportAppVcs(request, response);
    }

    @PostMapping("/command-template")
    @ApiOperation(value = "导出命令模板")
    @EventLog(EventType.DATA_EXPORT_COMMAND_TEMPLATE)
    public void exportCommandTemplate(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportCommandTemplate(request, response);
    }

    @PostMapping("/web-side-message")
    @ApiOperation(value = "导出站内信")
    @EventLog(EventType.DATA_EXPORT_WEB_SIDE_MESSAGE)
    public void exportWebSideMessage(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        dataExportService.exportWebSideMessage(request, response);
    }

    @PostMapping("/event-log")
    @ApiOperation(value = "导出用户操作日志")
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
