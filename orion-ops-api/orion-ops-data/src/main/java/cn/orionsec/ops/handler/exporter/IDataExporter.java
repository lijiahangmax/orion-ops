/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.handler.exporter;

import cn.orionsec.kit.lang.utils.Exceptions;
import cn.orionsec.ops.constant.ExportType;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.entity.request.data.DataExportRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 数据导出器 接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/8 18:11
 */
public interface IDataExporter {

    /**
     * 执行导出
     *
     * @throws IOException IOException
     */
    void doExport() throws IOException;

    /**
     * 创建数据导出器
     *
     * @param exportType 导出类型
     * @param request    request
     * @param response   response
     * @return exporter
     */
    static IDataExporter create(ExportType exportType, DataExportRequest request, HttpServletResponse response) {
        switch (exportType) {
            case MACHINE_INFO:
                return new MachineInfoDataExporter(request, response);
            case MACHINE_PROXY:
                return new MachineProxyDataExporter(request, response);
            case TERMINAL_LOG:
                return new TerminalLogDataExporter(request, response);
            case MACHINE_ALARM_HISTORY:
                return new MachineAlarmHistoryDataExporter(request, response);
            case APP_PROFILE:
                return new AppProfileDataExporter(request, response);
            case APPLICATION:
                return new ApplicationDataExporter(request, response);
            case APP_REPOSITORY:
                return new AppRepositoryDataExporter(request, response);
            case COMMAND_TEMPLATE:
                return new CommandTemplateDataExporter(request, response);
            case USER_EVENT_LOG:
                return new UserEventLogDataExporter(request, response);
            case TAIL_FILE:
                return new TailFileDataExporter(request, response);
            case WEBHOOK:
                return new WebhookDataExporter(request, response);
            default:
                throw Exceptions.argument(MessageConst.UNKNOWN_EXPORT_TYPE);
        }
    }

}
