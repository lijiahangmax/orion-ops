package com.orion.ops.constant;

import com.orion.lang.utils.Strings;
import com.orion.lang.utils.time.Dates;

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

    public static final String MACHINE_TERMINAL_LOG_EXPORT_NAME = "终端日志导出-{}.xlsx";

    public static final String MACHINE_TAIL_FILE_EXPORT_NAME = "日志文件导出-{}.xlsx";

    public static final String PROFILE_EXPORT_NAME = "应用环境导出-{}.xlsx";

    public static final String APPLICATION_EXPORT_NAME = "应用信息导出-{}.xlsx";

    public static final String REPOSITORY_EXPORT_NAME = "应用版本仓库导出-{}.xlsx";

    public static final String COMMAND_TEMPLATE_EXPORT_NAME = "命令模板导出-{}.xlsx";

    public static final String USER_EVENT_LOG_EXPORT_NAME = "操作日志导出-{}.xlsx";

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
