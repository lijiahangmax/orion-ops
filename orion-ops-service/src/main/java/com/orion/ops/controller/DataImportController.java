package com.orion.ops.controller;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.office.excel.Excels;
import com.orion.ops.OrionOpsServiceApplication;
import com.orion.ops.annotation.*;
import com.orion.ops.consts.SchedulerPools;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.export.ImportType;
import com.orion.ops.entity.dto.DataImportDTO;
import com.orion.ops.entity.dto.MachineInfoImportDTO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.DataImportRequest;
import com.orion.ops.entity.vo.DataImportCheckVO;
import com.orion.ops.service.api.DataImportService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import com.orion.servlet.web.Servlets;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.Threads;
import com.orion.utils.io.Streams;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * 数据导入 controller
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 14:02
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/data-import")
public class DataImportController {

    @Resource
    private DataImportService dataImportService;

    /**
     * 获取导入模板
     */
    @GetMapping("/get-template")
    @IgnoreWrapper
    @IgnoreLog
    @IgnoreAuth
    public void getTemplate(Integer type, HttpServletResponse response) throws IOException {
        ImportType importType = Valid.notNull(ImportType.of(type));
        Servlets.setDownloadHeader(response, importType.getTemplateName());
        // 读取文件
        InputStream in = OrionOpsServiceApplication.class.getResourceAsStream(importType.getTemplatePath());
        Streams.transfer(in, response.getOutputStream());
    }

    /**
     * 检查导入信息
     */
    @RequestMapping("/check-data")
    @SuppressWarnings("unchecked")
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
        // 解析数据
        List<?> rows;
        try {
            rows = dataImportService.parseImportWorkbook(importType, workbook);
        } catch (Exception e) {
            throw Exceptions.parse(e);
        } finally {
            Excels.close(workbook);
        }
        // 检查数据
        switch (importType) {
            case MACHINE:
                return dataImportService.checkMachineImportData((List<MachineInfoImportDTO>) rows);
            default:
                throw Exceptions.unsupported();
        }
    }

    /**
     * 导入机器
     */
    @RequestMapping("/import-machine")
    @EventLog(EventType.DATA_IMPORT_MACHINE)
    public HttpWrapper<?> importMachineData(@RequestBody DataImportRequest request) {
        String token = Valid.notNull(request.getImportToken());
        // 导入
        this.asyncImportData(token, d -> dataImportService.importMachineData(d));
        return HttpWrapper.ok();
    }

    /**
     * 取消导入
     */
    @RequestMapping("/cancel-import")
    public HttpWrapper<?> cancelImportData(@RequestBody DataImportRequest request) {
        String token = request.getImportToken();
        if (Strings.isBlank(token)) {
            return HttpWrapper.ok();
        }
        dataImportService.clearImportToken(token);
        return HttpWrapper.ok();
    }

    /**
     * 异步导入
     *
     * @param token   token
     * @param handler 处理器
     */
    private void asyncImportData(String token, Consumer<DataImportDTO> handler) {
        // 获取 token 信息
        DataImportDTO importData = dataImportService.checkImportToken(token);
        UserDTO user = Currents.getUser();
        importData.setUserId(user.getId());
        importData.setUserName(user.getUsername());
        importData.setImportTime(new Date());
        // 异步执行导入
        Threads.start(() -> handler.accept(importData), SchedulerPools.ASYNC_IMPORT_SCHEDULER);
        // 清空token
        dataImportService.clearImportToken(token);
    }

}
