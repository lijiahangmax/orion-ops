package com.orion.ops.controller;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.system.SystemCleanType;
import com.orion.ops.consts.system.SystemConfigKey;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.entity.request.ConfigIpListRequest;
import com.orion.ops.entity.request.SystemFileCleanRequest;
import com.orion.ops.entity.request.SystemOptionRequest;
import com.orion.ops.entity.vo.IpListConfigVO;
import com.orion.ops.entity.vo.SystemAnalysisVO;
import com.orion.ops.entity.vo.SystemOptionVO;
import com.orion.ops.entity.vo.ThreadPoolMetricsVO;
import com.orion.ops.service.api.SystemService;
import com.orion.ops.utils.Valid;
import com.orion.servlet.web.Servlets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 系统设置 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 22:07
 */
@Api(tags = "系统设置")
@RestController
@RestWrapper
@RequestMapping("/orion/api/system")
public class SystemController {

    @Resource
    private SystemService systemService;

    @PostMapping("/ip-info")
    @ApiOperation(value = "获取ip信息")
    public IpListConfigVO getIpInfo(HttpServletRequest request) {
        return systemService.getIpInfo(Servlets.getRemoteAddr(request));
    }

    @PostMapping("/config-ip")
    @ApiOperation(value = "配置ip过滤器列表")
    @EventLog(EventType.CONFIG_IP_LIST)
    @RequireRole(RoleType.ADMINISTRATOR)
    public HttpWrapper<?> configIpList(@RequestBody ConfigIpListRequest request) {
        systemService.configIpFilterList(request);
        return HttpWrapper.ok();
    }

    @PostMapping("/clean-system-file")
    @ApiOperation(value = "清理系统文件")
    @EventLog(EventType.CLEAN_SYSTEM_FILE)
    @RequireRole(RoleType.ADMINISTRATOR)
    public HttpWrapper<?> cleanSystemFile(@RequestBody SystemFileCleanRequest request) {
        SystemCleanType cleanType = Valid.notNull(SystemCleanType.of(request.getCleanType()));
        systemService.cleanSystemFile(cleanType);
        return HttpWrapper.ok();
    }

    @PostMapping("/get-system-analysis")
    @ApiOperation(value = "获取系统分析信息")
    public SystemAnalysisVO getSystemAnalysis() {
        return systemService.getSystemAnalysis();
    }

    @PostMapping("/re-analysis")
    @ApiOperation(value = "重新进行系统统计分析")
    @EventLog(EventType.RE_ANALYSIS_SYSTEM)
    @RequireRole(RoleType.ADMINISTRATOR)
    public SystemAnalysisVO reAnalysisSystem() {
        systemService.analysisSystemSpace();
        return systemService.getSystemAnalysis();
    }

    @PostMapping("/update-system-option")
    @ApiOperation(value = "修改系统配置项")
    @EventLog(EventType.UPDATE_SYSTEM_OPTION)
    @RequireRole(RoleType.ADMINISTRATOR)
    public HttpWrapper<?> updateSystemOption(@RequestBody SystemOptionRequest request) {
        SystemConfigKey key = Valid.notNull(SystemConfigKey.of(request.getOption()));
        String value = key.getValue(Valid.notBlank(request.getValue()));
        systemService.updateSystemOption(key.getEnv(), value);
        return HttpWrapper.ok();
    }

    @PostMapping("/get-system-options")
    @ApiOperation(value = "获取系统配置项")
    @RequireRole(RoleType.ADMINISTRATOR)
    public SystemOptionVO getSystemOptions() {
        return systemService.getSystemOptions();
    }

    @PostMapping("/get-thread-metrics")
    @ApiOperation(value = "获取线程池指标")
    @RequireRole(RoleType.ADMINISTRATOR)
    public List<ThreadPoolMetricsVO> getThreadMetrics() {
        return systemService.getThreadPoolMetrics();
    }

}
