package com.orion.ops.constant.monitor;

import com.orion.ops.constant.Const;
import com.orion.ops.constant.PropertiesConst;
import com.orion.ops.utils.ResourceLoader;
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
     * agent 文件名称前缀
     */
    public static final String AGENT_FILE_NAME_PREFIX = "machine-monitor-agent";

    /**
     * 启动脚本资源路径
     */
    public static final String START_SCRIPT_PATH = "/templates/script/start-monitor-agent.sh";

    /**
     * 启动脚本资源内容
     */
    public static final String START_SCRIPT_VALUE = ResourceLoader.get(START_SCRIPT_PATH, MonitorConst.class);

    /**
     * 启动脚本文件名称
     */
    public static final String START_SCRIPT_FILE_NAME = "start-machine-monitor-agent.sh";

    @Value("${machine.monitor.latest.version}")
    private void setLatestVersion(String latestVersion) {
        MonitorConst.LATEST_VERSION = latestVersion;
    }

    @Value("${machine.monitor.default.url}")
    private void setDefaultUrlFormat(String defaultUrlFormat) {
        MonitorConst.DEFAULT_URL_FORMAT = defaultUrlFormat;
    }

    @Value("${machine.monitor.default.access.header}")
    private void setDefaultAccessHeader(String defaultAccessHeader) {
        MonitorConst.DEFAULT_ACCESS_HEADER = defaultAccessHeader;
    }

    @Value("${machine.monitor.default.access.token}")
    private void setDefaultAccessToken(String defaultAccessToken) {
        MonitorConst.DEFAULT_ACCESS_TOKEN = defaultAccessToken;
    }

    /**
     * agent 文件名称
     *
     * @return 获取 agent 文件名称
     */
    public static String getAgentFileName() {
        return AGENT_FILE_NAME_PREFIX
                + "_" + PropertiesConst.MACHINE_MONITOR_LATEST_VERSION
                + "." + Const.SUFFIX_JAR;
    }

}
