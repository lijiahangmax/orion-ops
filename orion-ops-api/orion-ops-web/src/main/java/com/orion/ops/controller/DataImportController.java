package com.orion.ops.controller;

import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.Threads;
import com.orion.lang.utils.io.Streams;
import com.orion.office.excel.Excels;
import com.orion.ops.OrionApplication;
import com.orion.ops.annotation.*;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.constant.SchedulerPools;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.ImportType;
import com.orion.ops.constant.user.RoleType;
import com.orion.ops.entity.dto.user.UserDTO;
import com.orion.ops.entity.importer.*;
import com.orion.ops.entity.request.data.DataImportRequest;
import com.orion.ops.entity.vo.data.DataImportCheckVO;
import com.orion.ops.service.api.DataImportService;
import com.orion.ops.utils.Currents;
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
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

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
                return dataImportService.checkMachineInfoImportData((List<MachineInfoImportDTO>) rows);
            case MACHINE_PROXY:
                return dataImportService.checkMachineProxyImportData((List<MachineProxyImportDTO>) rows);
            case MACHINE_TAIL_FILE:
                return dataImportService.checkMachineTailFileImportData((List<MachineTailFileImportDTO>) rows);
            case PROFILE:
                return dataImportService.checkAppProfileImportData((List<ApplicationProfileImportDTO>) rows);
            case APPLICATION:
                return dataImportService.checkApplicationInfoImportData((List<ApplicationImportDTO>) rows);
            case REPOSITORY:
                return dataImportService.checkAppRepositoryImportData((List<ApplicationRepositoryImportDTO>) rows);
            case COMMAND_TEMPLATE:
                return dataImportService.checkCommandTemplateImportData((List<CommandTemplateImportDTO>) rows);
            default:
                throw Exceptions.unsupported();
        }
    }

    @PostMapping("/import-machine")
    @ApiOperation(value = "导入机器信息")
    @EventLog(EventType.DATA_IMPORT_MACHINE)
    public HttpWrapper<?> importMachineInfoData(@RequestBody DataImportRequest request) {
        String token = Valid.notNull(request.getImportToken());
        // 导入
        this.asyncImportData(token, d -> dataImportService.importMachineInfoData(d));
        return HttpWrapper.ok();
    }

    @PostMapping("/import-machine-proxy")
    @ApiOperation(value = "导入机器代理")
    @EventLog(EventType.DATA_IMPORT_MACHINE_PROXY)
    public HttpWrapper<?> importMachineProxyData(@RequestBody DataImportRequest request) {
        String token = Valid.notNull(request.getImportToken());
        // 导入
        this.asyncImportData(token, d -> dataImportService.importMachineProxyData(d));
        return HttpWrapper.ok();
    }

    @PostMapping("/import-tail-file")
    @ApiOperation(value = "导入日志文件")
    @EventLog(EventType.DATA_IMPORT_TAIL_FILE)
    public HttpWrapper<?> importMachineTailFileData(@RequestBody DataImportRequest request) {
        String token = Valid.notNull(request.getImportToken());
        // 导入
        this.asyncImportData(token, d -> dataImportService.importMachineTailFileData(d));
        return HttpWrapper.ok();
    }

    @PostMapping("/import-app-profile")
    @ApiOperation(value = "导入应用环境")
    @EventLog(EventType.DATA_IMPORT_PROFILE)
    @RequireRole(RoleType.ADMINISTRATOR)
    public HttpWrapper<?> importAppProfileData(@RequestBody DataImportRequest request) {
        String token = Valid.notNull(request.getImportToken());
        // 导入
        this.asyncImportData(token, d -> dataImportService.importAppProfileData(d));
        return HttpWrapper.ok();
    }

    @PostMapping("/import-application")
    @ApiOperation(value = "导入应用信息")
    @EventLog(EventType.DATA_IMPORT_APPLICATION)
    public HttpWrapper<?> importApplicationData(@RequestBody DataImportRequest request) {
        String token = Valid.notNull(request.getImportToken());
        // 导入
        this.asyncImportData(token, d -> dataImportService.importApplicationData(d));
        return HttpWrapper.ok();
    }

    @PostMapping("/import-repository")
    @ApiOperation(value = "导入应用版本仓库")
    @EventLog(EventType.DATA_IMPORT_REPOSITORY)
    public HttpWrapper<?> importRepositoryData(@RequestBody DataImportRequest request) {
        String token = Valid.notNull(request.getImportToken());
        // 导入
        this.asyncImportData(token, d -> dataImportService.importRepositoryData(d));
        return HttpWrapper.ok();
    }

    @PostMapping("/import-command-template")
    @ApiOperation(value = "导入命令模板")
    @EventLog(EventType.DATA_IMPORT_TAIL_FILE)
    public HttpWrapper<?> importCommandTemplateData(@RequestBody DataImportRequest request) {
        String token = Valid.notNull(request.getImportToken());
        // 导入
        this.asyncImportData(token, d -> dataImportService.importCommandTemplateData(d));
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
