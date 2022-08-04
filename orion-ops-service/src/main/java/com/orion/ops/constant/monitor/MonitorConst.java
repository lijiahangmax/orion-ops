package com.orion.ops.constant.monitor;

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

    @Value("${machine.monitor.latest.version}")
    public void setLatestVersion(String latestVersion) {
        LATEST_VERSION = latestVersion;
    }

    @Value("${machine.monitor.default.url}")
    public void setDefaultUrlFormat(String defaultUrlFormat) {
        DEFAULT_URL_FORMAT = defaultUrlFormat;
    }

    @Value("${machine.monitor.default.access.header}")
    public void setDefaultAccessHeader(String defaultAccessHeader) {
        DEFAULT_ACCESS_HEADER = defaultAccessHeader;
    }

    @Value("${machine.monitor.default.access.token}")
    public void setDefaultAccessToken(String defaultAccessToken) {
        DEFAULT_ACCESS_TOKEN = defaultAccessToken;
    }

}
