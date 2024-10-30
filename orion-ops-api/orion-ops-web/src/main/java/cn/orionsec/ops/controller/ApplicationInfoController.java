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
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.app.ActionType;
import cn.orionsec.ops.constant.app.StageType;
import cn.orionsec.ops.constant.app.TransferMode;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.app.*;
import cn.orionsec.ops.entity.vo.app.ApplicationDetailVO;
import cn.orionsec.ops.entity.vo.app.ApplicationInfoVO;
import cn.orionsec.ops.entity.vo.app.ApplicationMachineVO;
import cn.orionsec.ops.service.api.ApplicationInfoService;
import cn.orionsec.ops.service.api.ApplicationMachineService;
import cn.orionsec.ops.utils.Valid;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.utils.Exceptions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用信息 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 17:54
 */
@Api(tags = "应用信息")
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-info")
public class ApplicationInfoController {

    @Resource
    private ApplicationInfoService applicationService;

    @Resource
    private ApplicationMachineService applicationMachineService;

    @DemoDisableApi
    @PostMapping("/add")
    @ApiOperation(value = "添加应用")
    @EventLog(EventType.ADD_APP)
    public Long insertApp(@RequestBody ApplicationInfoRequest request) {
        Valid.allNotBlank(request.getName(), request.getTag());
        return applicationService.insertApp(request);
    }

    @DemoDisableApi
    @PostMapping("/update")
    @ApiOperation(value = "更新应用")
    @EventLog(EventType.UPDATE_APP)
    public Integer updateApp(@RequestBody ApplicationInfoRequest request) {
        Valid.notNull(request.getId());
        return applicationService.updateApp(request);
    }

    @PostMapping("/sort")
    @ApiOperation(value = "更新排序")
    public Integer updateAppSort(@RequestBody ApplicationInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        Integer adjust = Valid.in(request.getSortAdjust(), Const.INCREMENT, Const.DECREMENT);
        return applicationService.updateAppSort(id, Const.INCREMENT.equals(adjust));
    }

    @DemoDisableApi
    @PostMapping("/delete")
    @ApiOperation(value = "删除应用")
    @EventLog(EventType.DELETE_APP)
    public Integer deleteApp(@RequestBody ApplicationInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationService.deleteApp(id);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取应用列表")
    public DataGrid<ApplicationInfoVO> listApp(@RequestBody ApplicationInfoRequest request) {
        return applicationService.listApp(request);
    }

    @PostMapping("/list-machine")
    @ApiOperation(value = "获取应用机器列表")
    public List<ApplicationMachineVO> listAppMachines(@RequestBody ApplicationInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        Long profileId = Valid.notNull(request.getProfileId());
        return applicationService.getAppMachines(id, profileId);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取详情应用")
    public ApplicationDetailVO appDetail(@RequestBody ApplicationInfoRequest request) {
        Long appId = Valid.notNull(request.getId());
        return applicationService.getAppDetail(appId, request.getProfileId());
    }

    @PostMapping("/config")
    @ApiOperation(value = "配置应用")
    @EventLog(EventType.CONFIG_APP)
    public HttpWrapper<?> configApp(@RequestBody ApplicationConfigRequest request) {
        Valid.notNull(request.getAppId());
        Valid.notNull(request.getProfileId());
        this.checkConfig(request);
        applicationService.configAppProfile(request);
        return HttpWrapper.ok();
    }

    @PostMapping("/sync")
    @ApiOperation(value = "同步配置")
    @EventLog(EventType.SYNC_APP)
    public HttpWrapper<?> syncAppConfig(@RequestBody ApplicationSyncConfigRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        List<Long> targetProfileList = Valid.notEmpty(request.getTargetProfileIdList());
        applicationService.syncAppProfileConfig(appId, profileId, targetProfileList);
        return HttpWrapper.ok();
    }

    @DemoDisableApi
    @PostMapping("/copy")
    @ApiOperation(value = "复制应用")
    @EventLog(EventType.COPY_APP)
    public HttpWrapper<?> copyApplication(@RequestBody ApplicationInfoRequest request) {
        Long appId = Valid.notNull(request.getId());
        applicationService.copyApplication(appId);
        return HttpWrapper.ok();
    }

    @DemoDisableApi
    @PostMapping("/delete-machine")
    @ApiOperation(value = "删除发布机器")
    public Integer deleteAppMachine(@RequestBody ApplicationInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationMachineService.deleteById(id);
    }

    @PostMapping("/get-machine-id")
    @ApiOperation(value = "获取发布机器id")
    public List<Long> getAppMachineId(@RequestBody ApplicationInfoRequest request) {
        Long appId = Valid.notNull(request.getId());
        Long profileId = Valid.notNull(request.getProfileId());
        return applicationMachineService.getAppProfileMachineIdList(appId, profileId, true);
    }

    /**
     * 检查配置
     *
     * @param request request
     */
    private void checkConfig(ApplicationConfigRequest request) {
        StageType stageType = Valid.notNull(StageType.of(request.getStageType()));
        List<ApplicationConfigActionRequest> actions;
        ApplicationConfigEnvRequest env = Valid.notNull(request.getEnv());
        if (StageType.BUILD.equals(stageType)) {
            // 构建检查产物路径
            Valid.notBlank(env.getBundlePath());
            // 检查操作
            actions = Valid.notEmpty(request.getBuildActions());
        } else if (StageType.RELEASE.equals(stageType)) {
            // 发布序列
            Valid.notNull(env.getReleaseSerial());
            // 异常处理
            Valid.notNull(env.getExceptionHandler());
            // 发布检查机器id
            Valid.notEmpty(request.getMachineIdList());
            // 检查操作
            actions = Valid.notEmpty(request.getReleaseActions());
        } else {
            throw Exceptions.unsupported();
        }
        // 检查操作
        for (ApplicationConfigActionRequest action : actions) {
            Valid.notBlank(action.getName());
            ActionType actionType = Valid.notNull(ActionType.of(action.getType(), stageType.getType()));
            // 检查命令
            if (ActionType.BUILD_COMMAND.equals(actionType) || ActionType.RELEASE_COMMAND.equals(actionType)) {
                Valid.notBlank(action.getCommand());
            }
            // 检查传输 scp 命令
            if (ActionType.RELEASE_TRANSFER.equals(actionType)
                    && TransferMode.SCP.getValue().equals(env.getTransferMode())) {
                Valid.notBlank(action.getCommand());
            }
        }
        // 检查检出操作唯一性
        int checkoutActionCount = actions.stream()
                .map(ApplicationConfigActionRequest::getType)
                .map(ActionType::of)
                .filter(ActionType.BUILD_CHECKOUT::equals)
                .mapToInt(s -> Const.N_1)
                .sum();
        Valid.lte(checkoutActionCount, 1, MessageConst.CHECKOUT_ACTION_PRESENT);
        // 检查传输操作唯一性
        int transferActionCount = actions.stream()
                .map(ApplicationConfigActionRequest::getType)
                .map(ActionType::of)
                .filter(ActionType.RELEASE_TRANSFER::equals)
                .mapToInt(s -> Const.N_1)
                .sum();
        Valid.lte(transferActionCount, 1, MessageConst.TRANSFER_ACTION_PRESENT);
        if (transferActionCount != 0) {
            // 传输目录
            Valid.notBlank(env.getTransferPath());
        }
    }

}
