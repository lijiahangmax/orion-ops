package com.orion.ops.consts.export;

import com.orion.utils.Strings;
import com.orion.utils.time.Dates;

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

    public static final String APP_PROFILE_EXPORT_NAME = "应用环境导出-{}.xlsx";

    public static final String APPLICATION_EXPORT_NAME = "应用信息导出-{}.xlsx";

    public static final String APP_VCS_EXPORT_NAME = "应用版本仓库导出-{}.xlsx";


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
