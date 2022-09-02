package com.orion.ops.service.api;

import com.orion.ops.entity.request.data.DataExportRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 数据导出服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 16:18
 */
public interface DataExportService {

    /**
     * 导出机器
     *
     * @param request  request
     * @param response response
     * @throws IOException IOException
     */
    void exportMachine(DataExportRequest request, HttpServletResponse response) throws IOException;

    /**
     * 导出机器代理
     *
     * @param request  request
     * @param response response
     * @throws IOException IOException
     */
    void exportMachineProxy(DataExportRequest request, HttpServletResponse response) throws IOException;

    /**
     * 导出终端日志
     *
     * @param request  request
     * @param response response
     * @throws IOException IOException
     */
    void exportMachineTerminalLog(DataExportRequest request, HttpServletResponse response) throws IOException;

    /**
     * 导出机器日志
     *
     * @param request  request
     * @param response response
     * @throws IOException IOException
     */
    void exportMachineTailFile(DataExportRequest request, HttpServletResponse response) throws IOException;

    /**
     * 导出应用环境
     *
     * @param request  request
     * @param response response
     * @throws IOException IOException
     */
    void exportAppProfile(DataExportRequest request, HttpServletResponse response) throws IOException;

    /**
     * 导出应用
     *
     * @param request  request
     * @param response response
     * @throws IOException IOException
     */
    void exportApplication(DataExportRequest request, HttpServletResponse response) throws IOException;

    /**
     * 导出仓库
     *
     * @param request  request
     * @param response response
     * @throws IOException IOException
     */
    void exportAppRepository(DataExportRequest request, HttpServletResponse response) throws IOException;

    /**
     * 导出命令模板
     *
     * @param request  request
     * @param response response
     * @throws IOException IOException
     */
    void exportCommandTemplate(DataExportRequest request, HttpServletResponse response) throws IOException;

    /**
     * 导出操作日志
     *
     * @param request  request
     * @param response response
     * @throws IOException IOException
     */
    void exportEventLog(DataExportRequest request, HttpServletResponse response) throws IOException;

}
