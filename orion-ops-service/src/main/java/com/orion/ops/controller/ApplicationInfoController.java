package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.entity.request.*;
import com.orion.ops.entity.vo.ApplicationDetailVO;
import com.orion.ops.entity.vo.ApplicationInfoVO;
import com.orion.ops.entity.vo.ApplicationMachineVO;
import com.orion.ops.service.api.ApplicationInfoService;
import com.orion.ops.service.api.ApplicationMachineService;
import com.orion.ops.utils.Valid;
import com.orion.utils.Exceptions;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 17:54
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/app-info")
public class ApplicationInfoController {

    @Resource
    private ApplicationInfoService applicationService;

    @Resource
    private ApplicationMachineService applicationMachineService;

    /**
     * 添加应用
     */
    @RequestMapping("/add")
    @EventLog(EventType.ADD_APP)
    public Long insertApp(@RequestBody ApplicationInfoRequest request) {
        Valid.allNotBlank(request.getName(), request.getTag());
        return applicationService.insertApp(request);
    }

    /**
     * 更新应用
     */
    @RequestMapping("/update")
    @EventLog(EventType.UPDATE_APP)
    public Integer updateApp(@RequestBody ApplicationInfoRequest request) {
        Valid.notNull(request.getId());
        return applicationService.updateApp(request);
    }

    /**
     * 更新排序
     */
    @RequestMapping("/sort")
    public Integer updateAppSort(@RequestBody ApplicationInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        Integer adjust = Valid.in(request.getSortAdjust(), Const.INCREMENT, Const.DECREMENT);
        return applicationService.updateAppSort(id, Const.INCREMENT.equals(adjust));
    }

    /**
     * 删除应用
     */
    @RequestMapping("/delete")
    @EventLog(EventType.DELETE_APP)
    public Integer deleteApp(@RequestBody ApplicationInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationService.deleteApp(id);
    }

    /**
     * 应用列表
     */
    @RequestMapping("/list")
    public DataGrid<ApplicationInfoVO> listApp(@RequestBody ApplicationInfoRequest request) {
        return applicationService.listApp(request);
    }

    /**
     * 应用机器列表
     */
    @RequestMapping("/list-machine")
    public List<ApplicationMachineVO> listAppMachines(@RequestBody ApplicationInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        Long profileId = Valid.notNull(request.getProfileId());
        return applicationService.getAppMachines(id, profileId);
    }

    /**
     * 详情应用
     */
    @RequestMapping("/detail")
    public ApplicationDetailVO appDetail(@RequestBody ApplicationInfoRequest request) {
        Long appId = Valid.notNull(request.getId());
        return applicationService.getAppDetail(appId, request.getProfileId());
    }

    /**
     * 配置应用
     */
    @RequestMapping("/config")
    @EventLog(EventType.CONFIG_APP)
    public HttpWrapper<?> configApp(@RequestBody ApplicationConfigRequest request) {
        Valid.notNull(request.getAppId());
        Valid.notNull(request.getProfileId());
        this.checkConfig(request);
        applicationService.configAppProfile(request);
        return HttpWrapper.ok();
    }

    /**
     * 同步配置
     */
    @RequestMapping("/sync")
    @EventLog(EventType.SYNC_APP)
    public HttpWrapper<?> syncAppConfig(@RequestBody ApplicationSyncConfigRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        List<Long> targetProfileList = Valid.notEmpty(request.getTargetProfileIdList());
        applicationService.syncAppProfileConfig(appId, profileId, targetProfileList);
        return HttpWrapper.ok();
    }

    /**
     * 复制应用
     */
    @RequestMapping("/copy")
    @EventLog(EventType.COPY_APP)
    public HttpWrapper<?> copyApplication(@RequestBody ApplicationInfoRequest request) {
        Long appId = Valid.notNull(request.getId());
        applicationService.copyApplication(appId);
        return HttpWrapper.ok();
    }

    /**
     * 删除发布机器
     */
    @RequestMapping("/delete-machine")
    public Integer deleteAppMachine(@RequestBody ApplicationInfoRequest request) {
        Long id = Valid.notNull(request.getId());
        return applicationMachineService.deleteById(id);
    }

    /**
     * 获取发布机器id
     */
    @RequestMapping("/get-machine-id")
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
            if (ActionType.BUILD_COMMAND.equals(actionType)
                    || ActionType.RELEASE_COMMAND.equals(actionType)) {
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
