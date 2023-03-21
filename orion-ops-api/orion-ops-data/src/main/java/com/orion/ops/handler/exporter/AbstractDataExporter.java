package com.orion.ops.handler.exporter;

import com.orion.lang.utils.Strings;
import com.orion.lang.utils.io.FileWriters;
import com.orion.lang.utils.io.Files1;
import com.orion.office.excel.writer.exporting.ExcelExport;
import com.orion.ops.constant.ExportType;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.entity.request.data.DataExportRequest;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.EventParamsHolder;
import com.orion.ops.utils.PathBuilders;
import com.orion.web.servlet.web.Servlets;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 数据导出器 抽象类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/8 18:13
 */
public abstract class AbstractDataExporter<T> implements IDataExporter {

    /**
     * 导出类型
     */
    protected final ExportType exportType;

    /**
     * 请求参数
     */
    protected final DataExportRequest request;

    /**
     * http response
     */
    protected final HttpServletResponse response;

    protected ExcelExport<T> exporter;

    public AbstractDataExporter(ExportType exportType, DataExportRequest request, HttpServletResponse response) {
        this.exportType = exportType;
        this.request = request;
        this.response = response;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void doExport() throws IOException {
        // 查询数据
        List<T> exportList = this.queryData();
        // 初始化导出器
        this.exporter = new ExcelExport<T>((Class<T>) exportType.getDataClass()).init();
        exporter.addRows(exportList);
        // 写入到 workbook
        this.writeWorkbook();
        // 设置日志参数
        this.setEventParams();
    }

    /**
     * 查询数据
     *
     * @return rows
     */
    protected abstract List<T> queryData();

    /**
     * 写入 workbook
     *
     * @throws IOException IOException
     */
    protected void writeWorkbook() throws IOException {
        // 设置 http 响应头
        Servlets.setDownloadHeader(response, exportType.getNameSupplier().get());
        // 写入 workbook 到 byte
        ByteArrayOutputStream store = new ByteArrayOutputStream();
        String password = request.getProtectPassword();
        if (!Strings.isBlank(password)) {
            exporter.write(store, password);
        } else {
            exporter.write(store);
        }
        // 设置 http 返回
        byte[] excelData = store.toByteArray();
        response.getOutputStream().write(excelData);
        // 保存至本地
        String exportPath = PathBuilders.getExportDataJsonPath(Currents.getUserId(), exportType.getType(), Strings.def(password));
        String path = Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), exportPath);
        FileWriters.write(path, excelData);
    }

    /**
     * 设置日志参数
     */
    protected void setEventParams() {
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.EXPORT_PASSWORD, request.getExportPassword());
        EventParamsHolder.addParam(EventKeys.PROTECT, request.getProtectPassword() != null);
        EventParamsHolder.addParam(EventKeys.COUNT, exporter.getRows());
        EventParamsHolder.addParam(EventKeys.EXPORT_TYPE, exportType.getType());
        EventParamsHolder.addParam(EventKeys.LABEL, exportType.getLabel());
    }

}
