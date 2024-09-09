package com.orion.ops.controller;

import com.orion.ops.annotation.DemoDisableApi;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.constant.ExportType;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.data.DataExportRequest;
import com.orion.ops.handler.exporter.IDataExporter;
import com.orion.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @DemoDisableApi
    @PostMapping("/export")
    @ApiOperation(value = "导出数据")
    @EventLog(EventType.DATA_EXPORT)
    public void exportData(@RequestBody DataExportRequest request, HttpServletResponse response) throws IOException {
        ExportType exportType = Valid.notNull(ExportType.of(request.getExportType()));
        IDataExporter.create(exportType, request, response).doExport();
    }

}
