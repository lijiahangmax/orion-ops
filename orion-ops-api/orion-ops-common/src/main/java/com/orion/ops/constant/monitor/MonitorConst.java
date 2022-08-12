package com.orion.ops.constant.monitor;

import com.orion.ops.constant.Const;
import com.orion.ops.constant.PropertiesConst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 监控常量
 * <p>
 * 禁止手动赋值
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/4 15:37
 */
@Component
public class MonitorConst {

    private MonitorConst() {
    }

    /**
     * 最新版本
     */
    public static String LATEST_VERSION;

    /**
     * 默认 url
     */
    public static String DEFAULT_URL_FORMAT;

    /**
     * 默认请求头
     */
    public static String DEFAULT_ACCESS_HEADER;

    /**
     * 默认 accessToken
     */
    public static String DEFAULT_ACCESS_TOKEN;

    /**
     * 文件名称前缀
     */
    public static final String FILE_NAME_PREFIX = "machine-monitor-agent";

    /**
     * 文件名称
     */
    public static final String FILE_NAME = FILE_NAME_PREFIX
            + "_" + PropertiesConst.MACHINE_MONITOR_LATEST_VERSION
            + "." + Const.SUFFIX_JAR;

    /**
     * 启动脚本名称
     */
    public static final String START_SCRIPT_NAME = "agent-start.sh";

    @Value("${machine.monitor.latest.version}")
    public void setLatestVersion(String latestVersion) {
        MonitorConst.LATEST_VERSION = latestVersion;
    }

    @Value("${machine.monitor.default.url}")
    public void setDefaultUrlFormat(String defaultUrlFormat) {
        MonitorConst.DEFAULT_URL_FORMAT = defaultUrlFormat;
    }

    @Value("${machine.monitor.default.access.header}")
    public void setDefaultAccessHeader(String defaultAccessHeader) {
        MonitorConst.DEFAULT_ACCESS_HEADER = defaultAccessHeader;
    }

    @Value("${machine.monitor.default.access.token}")
    public void setDefaultAccessToken(String defaultAccessToken) {
        MonitorConst.DEFAULT_ACCESS_TOKEN = defaultAccessToken;
    }

}
