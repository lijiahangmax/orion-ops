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
package cn.orionsec.ops.constant;

import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.time.Dates;

/**
 * 导出常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 16:14
 */
public class ExportConst {

    private ExportConst() {
    }

    public static final String MACHINE_EXPORT_NAME = "机器信息导出-{}.xlsx";

    public static final String MACHINE_PROXY_EXPORT_NAME = "机器代理导出-{}.xlsx";

    public static final String MACHINE_ALARM_HISTORY_EXPORT_NAME = "机器报警记录导出-{}.xlsx";

    public static final String TERMINAL_LOG_EXPORT_NAME = "终端日志导出-{}.xlsx";

    public static final String TAIL_FILE_EXPORT_NAME = "日志文件导出-{}.xlsx";

    public static final String APP_PROFILE_EXPORT_NAME = "应用环境导出-{}.xlsx";

    public static final String APPLICATION_EXPORT_NAME = "应用信息导出-{}.xlsx";

    public static final String APP_REPOSITORY_EXPORT_NAME = "应用版本仓库导出-{}.xlsx";

    public static final String COMMAND_TEMPLATE_EXPORT_NAME = "命令模板导出-{}.xlsx";

    public static final String USER_EVENT_LOG_EXPORT_NAME = "操作日志导出-{}.xlsx";

    public static final String WEBHOOK_EXPORT_NAME = "webhook导出-{}.xlsx";

    /**
     * 格式导出文件名
     *
     * @param name name
     * @return name
     */
    public static String getFileName(String name) {
        return Strings.format(name, Dates.current(Dates.YMD_HM2));
    }

}
