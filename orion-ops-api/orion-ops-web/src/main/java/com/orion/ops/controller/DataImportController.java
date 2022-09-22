package com.orion.ops.controller;

import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.io.Streams;
import com.orion.office.excel.Excels;
import com.orion.ops.OrionApplication;
import com.orion.ops.annotation.*;
import com.orion.ops.constant.ImportType;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.importer.DataImportDTO;
import com.orion.ops.entity.request.data.DataImportRequest;
import com.orion.ops.entity.vo.data.DataImportCheckVO;
import com.orion.ops.handler.importer.checker.IDataChecker;
import com.orion.ops.handler.importer.impl.IDataImporter;
import com.orion.ops.service.api.DataImportService;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.ops.utils.Valid;
import com.orion.web.servlet.web.Servlets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * 数据导入 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 14:02
 */
@Api(tags = "数据导入")
@RestController
@RestWrapper
@RequestMapping("/orion/api/data-import")
public class DataImportController {

    @Resource
    private DataImportService dataImportService;

    @IgnoreWrapper
    @IgnoreLog
    @IgnoreAuth
    @GetMapping("/get-template")
    @ApiOperation(value = "获取导入模板")
    public void getTemplate(Integer type, HttpServletResponse response) throws IOException {
        ImportType importType = Valid.notNull(ImportType.of(type));
        String templateName = importType.getTemplateName();
        Servlets.setDownloadHeader(response, templateName);
        // 读取文件
        InputStream in = OrionApplication.class.getResourceAsStream(importType.getTemplatePath());
        ServletOutputStream out = response.getOutputStream();
        if (in == null) {
            out.write(Strings.bytes(Strings.format(MessageConst.FILE_NOT_FOUND, templateName)));
            return;
        }
        Streams.transfer(in, out);
    }

    @PostMapping("/check-data")
    @ApiOperation(value = "检查导入信息")
    public DataImportCheckVO checkImportData(@RequestParam("file") MultipartFile file,
                                             @RequestParam("type") Integer type,
                                             @RequestParam(name = "protectPassword", required = false) String protectPassword) throws IOException {
        ImportType importType = Valid.notNull(ImportType.of(type));
        Workbook workbook;
        if (Strings.isBlank(protectPassword)) {
            workbook = Excels.openWorkbook(file.getInputStream());
        } else {
            workbook = Excels.openWorkbook(file.getInputStream(), protectPassword);
        }
        // 检查数据
        return IDataChecker.create(importType, workbook).doCheck();
    }

    @PostMapping("/import")
    @ApiOperation(value = "导入数据")
    @EventLog(EventType.DATA_IMPORT)
    public HttpWrapper<?> importData(@RequestBody DataImportRequest request) {
        String token = Valid.notNull(request.getImportToken());
        // 读取导入数据
        DataImportDTO importData = dataImportService.checkImportToken(token);
        // 执行导入操作
        IDataImporter.create(importData).doImport();
        // 设置日志参数
        ImportType importType = ImportType.of(importData.getType());
        EventParamsHolder.addParam(EventKeys.TOKEN, token);
        EventParamsHolder.addParam(EventKeys.TYPE, importType.getType());
        EventParamsHolder.addParam(EventKeys.LABEL, importType.getLabel());
        return HttpWrapper.ok();
    }

    @PostMapping("/cancel-import")
    @ApiOperation(value = "取消导入")
    public HttpWrapper<?> cancelImportData(@RequestBody DataImportRequest request) {
        String token = request.getImportToken();
        if (Strings.isBlank(token)) {
            return HttpWrapper.ok();
        }
        dataImportService.clearImportToken(token);
        return HttpWrapper.ok();
    }

}
