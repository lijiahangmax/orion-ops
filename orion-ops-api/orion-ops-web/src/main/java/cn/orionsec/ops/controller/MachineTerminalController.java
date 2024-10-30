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
package cn.orionsec.ops.controller;

import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.RequireRole;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.ResultCode;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.constant.user.RoleType;
import cn.orionsec.ops.entity.request.machine.MachineTerminalLogRequest;
import cn.orionsec.ops.entity.request.machine.MachineTerminalManagerRequest;
import cn.orionsec.ops.entity.request.machine.MachineTerminalRequest;
import cn.orionsec.ops.entity.vo.machine.*;
import cn.orionsec.ops.handler.terminal.manager.TerminalSessionManager;
import cn.orionsec.ops.service.api.MachineTerminalService;
import cn.orionsec.ops.utils.Valid;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.codec.Base64s;
import com.orion.lang.utils.io.FileReaders;
import com.orion.net.remote.TerminalType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * 机器终端 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 21:45
 */
@Api(tags = "机器终端")
@RestController
@RestWrapper
@RequestMapping("/orion/api/terminal")
public class MachineTerminalController {

    @Resource
    private MachineTerminalService machineTerminalService;

    @Resource
    private TerminalSessionManager terminalSessionManager;

    @PostMapping("/access")
    @ApiOperation(value = "获取终端accessToken")
    @EventLog(EventType.OPEN_TERMINAL)
    public TerminalAccessVO getTerminalAccessToken(@RequestBody MachineTerminalRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        return machineTerminalService.getAccessConfig(machineId);
    }

    @GetMapping("/support/pty")
    @ApiOperation(value = "获取支持的终端类型")
    public String[] getSupportedPty() {
        return Arrays.stream(TerminalType.values())
                .map(TerminalType::getType)
                .toArray(String[]::new);
    }

    @GetMapping("/get/{machineId}")
    @ApiOperation(value = "获取终端配置")
    public MachineTerminalVO getSetting(@PathVariable Long machineId) {
        return machineTerminalService.getMachineConfig(machineId);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改终端配置")
    @EventLog(EventType.UPDATE_TERMINAL_CONFIG)
    public Integer updateSetting(@RequestBody MachineTerminalRequest request) {
        Valid.notNull(request.getId());
        String terminalType = request.getTerminalType();
        if (!Strings.isBlank(terminalType)) {
            Valid.notNull(TerminalType.of(terminalType), MessageConst.INVALID_PTY);
        }
        if (request.getEnableWebLink() != null) {
            Valid.in(request.getEnableWebLink(), Const.ENABLE, Const.DISABLE);
        }
        return machineTerminalService.updateSetting(request);
    }

    @PostMapping("/log/list")
    @ApiOperation(value = "获取终端日志列表")
    public DataGrid<MachineTerminalLogVO> accessLogList(@RequestBody MachineTerminalLogRequest request) {
        return machineTerminalService.listAccessLog(request);
    }

    @PostMapping("/log/delete")
    @ApiOperation(value = "删除终端日志")
    @EventLog(EventType.DELETE_TERMINAL_LOG)
    public Integer deleteLog(@RequestBody MachineTerminalLogRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return machineTerminalService.deleteTerminalLog(idList);
    }

    @PostMapping("/log/screen")
    @ApiOperation(value = "获取终端录屏文件 base64")
    public String getLogScreen(@RequestBody MachineTerminalLogRequest request) {
        Long id = Valid.notNull(request.getId());
        String path = machineTerminalService.getTerminalScreenFilePath(id);
        if (path == null) {
            throw Exceptions.httpWrapper(HttpWrapper.of(ResultCode.FILE_MISSING));
        }
        Path file = Paths.get(path);
        if (!Files.exists(file)) {
            throw Exceptions.httpWrapper(HttpWrapper.of(ResultCode.FILE_MISSING));
        }
        return Base64s.encodeToString(FileReaders.readAllBytesFast(path));
    }

    @PostMapping("/manager/session")
    @ApiOperation(value = "获取终端会话列表")
    @RequireRole(RoleType.ADMINISTRATOR)
    public DataGrid<MachineTerminalManagerVO> sessionList(@RequestBody MachineTerminalManagerRequest request) {
        return terminalSessionManager.getOnlineTerminal(request);
    }

    @DemoDisableApi
    @PostMapping("/manager/offline")
    @ApiOperation(value = "强制下线终端会话")
    @RequireRole(RoleType.ADMINISTRATOR)
    @EventLog(EventType.FORCE_OFFLINE_TERMINAL)
    public void forceOffline(@RequestBody MachineTerminalManagerRequest request) {
        String token = Valid.notBlank(request.getToken());
        terminalSessionManager.forceOffline(token);
    }

    @PostMapping("/manager/watcher")
    @ApiOperation(value = "获取终端监视token")
    @RequireRole(RoleType.ADMINISTRATOR)
    public TerminalWatcherVO getTerminalWatcherToken(@RequestBody MachineTerminalManagerRequest request) {
        String token = Valid.notBlank(request.getToken());
        Integer readonly = Valid.notNull(request.getReadonly());
        return terminalSessionManager.getWatcherToken(token, readonly);
    }

}
