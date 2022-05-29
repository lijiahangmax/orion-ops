package com.orion.ops.controller;

import com.orion.ops.annotation.EventLog;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.export.ExportConst;
import com.orion.ops.entity.request.DataExportRequest;
import com.orion.ops.service.api.DataExportService;
import com.orion.servlet.web.Servlets;
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
        Servlets.setDownloadHeader(response, ExportConst.getFileName(ExportConst.MACHINE_EXPORT_NAME));
        dataExportService.exportMachine(request, response);
    }

}
