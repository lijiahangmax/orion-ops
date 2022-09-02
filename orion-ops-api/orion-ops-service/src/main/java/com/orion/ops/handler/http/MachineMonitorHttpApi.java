package com.orion.ops.handler.http;

import com.orion.http.support.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 机器监控插件 http api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/1 10:55
 */
@Getter
@AllArgsConstructor
public enum MachineMonitorHttpApi implements HttpApiDefined {

    /**
     * 端点 ping
     */
    ENDPOINT_PING("/orion/machine-monitor-agent/api/endpoint/ping", HttpMethod.GET),

    /**
     * 端点 version
     */
    ENDPOINT_VERSION("/orion/machine-monitor-agent/api/endpoint/version", HttpMethod.GET),

    /**
     * 端点 sync
     */
    ENDPOINT_SYNC("/orion/machine-monitor-agent/api/endpoint/sync", HttpMethod.POST),

    /**
     * 指标 获取机器基本指标
     */
    METRICS_BASE("/orion/machine-monitor-agent/api/metrics/base", HttpMethod.GET),

    /**
     * 指标 获取系统负载
     */
    METRICS_SYSTEM_LOAD("/orion/machine-monitor-agent/api/metrics/system-load", HttpMethod.GET),

    /**
     * 指标 获取硬盘名称
     */
    METRICS_DISK_NAME("/orion/machine-monitor-agent/api/metrics/disk-name", HttpMethod.GET),

    /**
     * 指标 获取 top 进程
     */
    METRICS_TOP_PROCESSES("/orion/machine-monitor-agent/api/metrics/top-processes", HttpMethod.GET),

    /**
     * 监控 获取cpu数据
     */
    MONITOR_CPU("/orion/machine-monitor-agent/api/monitor-statistic/cpu", HttpMethod.POST),

    /**
     * 监控 获取内存数据
     */
    MONITOR_MEMORY("/orion/machine-monitor-agent/api/monitor-statistic/memory", HttpMethod.POST),

    /**
     * 监控 获取网络数据
     */
    MONITOR_NET("/orion/machine-monitor-agent/api/monitor-statistic/net", HttpMethod.POST),

    /**
     * 监控 获取磁盘数据
     */
    MONITOR_DISK("/orion/machine-monitor-agent/api/monitor-statistic/disk", HttpMethod.POST),

    ;

    /**
     * 请求路径
     */
    private final String path;

    /**
     * 请求方法
     */
    private final HttpMethod method;

}
