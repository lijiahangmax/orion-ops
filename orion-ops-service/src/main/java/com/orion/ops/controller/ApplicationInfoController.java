package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.consts.app.VcsType;
import com.orion.ops.entity.request.*;
import com.orion.ops.entity.vo.*;
import com.orion.ops.service.api.ApplicationInfoService;
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

    /**
     * 添加应用
     */
    @RequestMapping("/add")
    public Long insertApp(@RequestBody ApplicationInfoRequest request) {
        Valid.notNull(request.getName());
        Valid.notNull(request.getTag());
        Valid.notNull(request.getSort());
        return applicationService.insertApp(request);
    }

    /**
     * 更新应用
     */
    @RequestMapping("/update")
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
     * 详情应用
     */
    @RequestMapping("/detail")
    public ApplicationDetailVO appDetail(@RequestBody ApplicationInfoRequest request) {
        Long appId = Valid.notNull(request.getId());
        Long profileId = Valid.notNull(request.getProfileId());
        return applicationService.getAppDetail(appId, profileId);
    }

    /**
     * 配置应用
     */
    @RequestMapping("/config")
    public HttpWrapper<?> configApp(@RequestBody ApplicationConfigRequest request) {
        Valid.notNull(request.getAppId());
        Valid.notNull(request.getProfileId());
        Valid.notNull(request.getEnv());
        Valid.notBlank(request.getEnv().getVcsCodePath());
        Valid.notBlank(request.getEnv().getVcsRootPath());
        Valid.notNull(VcsType.of(request.getEnv().getVcsType()));
        Valid.notBlank(request.getEnv().getDistPath());
        Valid.notEmpty(request.getMachineIdList());
        this.checkActionType(Valid.notEmpty(request.getActions()));
        applicationService.configAppProfile(request);
        return HttpWrapper.ok();
    }

    /**
     * 同步配置
     */
    @RequestMapping("/sync")
    public HttpWrapper<?> syncAppConfig(@RequestBody ApplicationSyncConfigRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        Long syncProfileId = Valid.notNull(request.getSyncProfileId());
        applicationService.syncAppProfileConfig(appId, profileId, syncProfileId);
        return HttpWrapper.ok();
    }

    /**
     * 复制应用
     */
    @RequestMapping("/copy")
    public HttpWrapper<?> copyApplication(@RequestBody ApplicationInfoRequest request) {
        Long appId = Valid.notNull(request.getId());
        applicationService.copyApplication(appId);
        return HttpWrapper.ok();
    }

    /**
     * 获取版本信息
     */
    @RequestMapping("/vcs/info")
    public ApplicationVcsInfoVO getVcsDefaultInfo(@RequestBody ApplicationVcsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return applicationService.getVcsInfo(appId, profileId);
    }

    /**
     * 获取分支列表
     */
    @RequestMapping("/vcs/branch")
    public List<ApplicationVcsBranchVO> getBranchList(@RequestBody ApplicationVcsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        return applicationService.getVcsBranchList(appId, profileId);
    }

    /**
     * 获取提交列表
     */
    @RequestMapping("/vcs/commit")
    public List<ApplicationVcsCommitVO> getCommitList(@RequestBody ApplicationVcsRequest request) {
        Long appId = Valid.notNull(request.getAppId());
        Long profileId = Valid.notNull(request.getProfileId());
        String branchName = Valid.notBlank(request.getBranchName());
        return applicationService.getVcsCommitList(appId, profileId, branchName);
    }

    /**
     * 检查配置步骤的合法性
     *
     * @param actions actions
     */
    private void checkActionType(List<ApplicationConfigDeployActionRequest> actions) {
        // 检查参数
        for (ApplicationConfigDeployActionRequest action : actions) {
            Valid.notBlank(action.getName());
            ActionType actionType = Valid.notNull(ActionType.of(action.getType(), true));
            Valid.isTrue(!ActionType.CONNECT.equals(actionType));
            if (ActionType.HOST_COMMAND.equals(actionType)
                    || ActionType.TARGET_COMMAND.equals(actionType)) {
                Valid.notBlank(action.getCommand());
            }
        }
        // 检查单一操作
        int checkoutIndex = -1;
        int transferIndex = -1;
        for (int i = 0; i < actions.size(); i++) {
            ActionType type = ActionType.of(actions.get(i).getType(), true);
            if (ActionType.CHECKOUT.equals(type)) {
                if (checkoutIndex == -1) {
                    checkoutIndex = i;
                } else {
                    throw Exceptions.invalidArgument(MessageConst.CHECKOUT_ACTION_PRESENT);
                }
            } else if (ActionType.TRANSFER.equals(type)) {
                if (transferIndex == -1) {
                    transferIndex = i;
                } else {
                    throw Exceptions.invalidArgument(MessageConst.TRANSFER_ACTION_PRESENT);
                }
            }
        }
        // 检查传输和检出的时序
        if (checkoutIndex == -1) {
            throw Exceptions.invalidArgument(MessageConst.CHECKOUT_ACTION_ABSENT);
        }
        if (transferIndex != -1 && transferIndex < checkoutIndex) {
            throw Exceptions.invalidArgument(MessageConst.TRANSFER_ACTION_WRONG_STEP);
        }
        // 检查先宿主再目标
        int lastTargetCommand = -1;
        for (int i = 0; i < actions.size(); i++) {
            ActionType type = ActionType.of(actions.get(i).getType(), true);
            if (ActionType.HOST_COMMAND.equals(type)
                    || ActionType.CHECKOUT.equals(type)
                    || ActionType.TRANSFER.equals(type)) {
                if (lastTargetCommand != -1) {
                    throw Exceptions.invalidArgument(MessageConst.HOST_ACTION_WRONG_STEP);
                }
            } else if (ActionType.TARGET_COMMAND.equals(type)) {
                lastTargetCommand = i;
            }
        }
    }

}
