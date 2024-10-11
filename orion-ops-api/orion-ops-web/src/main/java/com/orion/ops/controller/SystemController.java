/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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
package com.orion.ops.controller;

import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.ops.annotation.DemoDisableApi;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RequireRole;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.CnConst;
import com.orion.ops.constant.Const;
import com.orion.ops.constant.PropertiesConst;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.constant.system.SystemCleanType;
import com.orion.ops.constant.system.SystemConfigKey;
import com.orion.ops.constant.user.RoleType;
import com.orion.ops.entity.request.system.ConfigIpListRequest;
import com.orion.ops.entity.request.system.SystemFileCleanRequest;
import com.orion.ops.entity.request.system.SystemOptionRequest;
import com.orion.ops.entity.vo.system.*;
import com.orion.ops.service.api.SystemService;
import com.orion.ops.utils.Valid;
import com.orion.web.servlet.web.Servlets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/ip-info")
    @ApiOperation(value = "获取ip信息")
    public IpListConfigVO getIpInfo(HttpServletRequest request) {
        return systemService.getIpInfo(Servlets.getRemoteAddr(request));
    }

    @DemoDisableApi
    @PostMapping("/config-ip")
    @ApiOperation(value = "配置ip过滤器列表")
    @EventLog(EventType.CONFIG_IP_LIST)
    @RequireRole(RoleType.ADMINISTRATOR)
    public HttpWrapper<?> configIpList(@RequestBody ConfigIpListRequest request) {
        systemService.configIpFilterList(request);
        return HttpWrapper.ok();
    }

    @DemoDisableApi
    @PostMapping("/clean-system-file")
    @ApiOperation(value = "清理系统文件")
    @EventLog(EventType.CLEAN_SYSTEM_FILE)
    @RequireRole(RoleType.ADMINISTRATOR)
    public HttpWrapper<?> cleanSystemFile(@RequestBody SystemFileCleanRequest request) {
        SystemCleanType cleanType = Valid.notNull(SystemCleanType.of(request.getCleanType()));
        systemService.cleanSystemFile(cleanType);
        return HttpWrapper.ok();
    }

    @GetMapping("/get-system-analysis")
    @ApiOperation(value = "获取系统分析信息")
    public SystemAnalysisVO getSystemAnalysis() {
        return systemService.getSystemAnalysis();
    }

    @DemoDisableApi
    @GetMapping("/re-analysis")
    @ApiOperation(value = "重新进行系统统计分析")
    @EventLog(EventType.RE_ANALYSIS_SYSTEM)
    @RequireRole(RoleType.ADMINISTRATOR)
    public SystemAnalysisVO reAnalysisSystem() {
        systemService.analysisSystemSpace();
        return systemService.getSystemAnalysis();
    }

    @DemoDisableApi
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

    @GetMapping("/get-system-options")
    @ApiOperation(value = "获取系统配置项")
    @RequireRole(RoleType.ADMINISTRATOR)
    public SystemOptionVO getSystemOptions() {
        return systemService.getSystemOptions();
    }

    @GetMapping("/get-thread-metrics")
    @ApiOperation(value = "获取线程池指标")
    @RequireRole(RoleType.ADMINISTRATOR)
    public List<ThreadPoolMetricsVO> getThreadMetrics() {
        return systemService.getThreadPoolMetrics();
    }

    @GetMapping("/about")
    @ApiOperation(value = "获取应用信息")
    public SystemAboutVO getSystemAbout() {
        return SystemAboutVO.builder()
                .orionKitVersion(Const.ORION_KIT_VERSION)
                .orionOpsVersion(PropertiesConst.ORION_OPS_VERSION)
                .author(Const.ORION_AUTHOR)
                .authorCn(CnConst.ORION_OPS_AUTHOR)
                .build();
    }

}
