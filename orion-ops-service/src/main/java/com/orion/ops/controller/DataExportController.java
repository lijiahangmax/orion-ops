package com.orion.ops.controller;

import com.orion.ops.annotation.EventLog;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.entity.request.DataExportRequest;
import com.orion.ops.service.api.DataExportService;
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

}
