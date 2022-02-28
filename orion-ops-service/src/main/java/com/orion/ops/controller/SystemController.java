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
import com.orion.ops.service.api.SystemService;
import com.orion.ops.utils.Valid;
import com.orion.servlet.web.Servlets;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 系统配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 22:07
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/system")
public class SystemController {

    @Resource
    private SystemService systemService;

    /**
     * 获取 ip 配置
     */
    @RequestMapping("/ip-info")
    public IpListConfigVO getIpInfo(HttpServletRequest request) {
        return systemService.getIpInfo(Servlets.getRemoteAddr(request));
    }

    /**
     * 配置 ip 列表
     */
    @RequestMapping("/config-ip")
    @EventLog(EventType.CONFIG_IP_LIST)
    @RequireRole(RoleType.ADMINISTRATOR)
    public HttpWrapper<?> configIpList(@RequestBody ConfigIpListRequest request) {
        systemService.configIpList(request);
        return HttpWrapper.ok();
    }

    /**
     * 清理系统文件
     */
    @RequestMapping("/clean-system-file")
    @EventLog(EventType.CLEAN_SYSTEM_FILE)
    @RequireRole(RoleType.ADMINISTRATOR)
    public HttpWrapper<?> cleanSystemFile(@RequestBody SystemFileCleanRequest request) {
        SystemCleanType cleanType = Valid.notNull(SystemCleanType.of(request.getCleanType()));
        systemService.cleanSystemFile(cleanType);
        return HttpWrapper.ok();
    }

    /**
     * 获取系统分析信息
     */
    @RequestMapping("/get-system-analysis")
    public SystemAnalysisVO getSystemAnalysis() {
        return systemService.getSystemAnalysis();
    }

    /**
     * 重新进行系统统计分析
     */
    @RequestMapping("/re-analysis")
    @EventLog(EventType.RE_ANALYSIS_SYSTEM)
    @RequireRole(RoleType.ADMINISTRATOR)
    public SystemAnalysisVO reAnalysisSystem() {
        systemService.analysisSystemSpace();
        return systemService.getSystemAnalysis();
    }

    /**
     * 修改系统配置项
     */
    @RequestMapping("/update-system-option")
    @EventLog(EventType.UPDATE_SYSTEM_OPTION)
    @RequireRole(RoleType.ADMINISTRATOR)
    public HttpWrapper<?> updateSystemOption(@RequestBody SystemOptionRequest request) {
        SystemConfigKey key = Valid.notNull(SystemConfigKey.of(request.getOption()));
        String value = key.getValue(Valid.notBlank(request.getValue()));
        systemService.updateSystemOption(key.getEnv(), value);
        return HttpWrapper.ok();
    }

    /**
     * 获取系统配置项
     */
    @RequestMapping("/get-system-options")
    @RequireRole(RoleType.ADMINISTRATOR)
    public SystemOptionVO getSystemOptions() {
        return systemService.getSystemOptions();
    }

}
