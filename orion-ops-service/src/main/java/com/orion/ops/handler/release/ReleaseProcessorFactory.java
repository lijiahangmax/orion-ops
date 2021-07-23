package com.orion.ops.handler.release;

import com.orion.function.select.Branches;
import com.orion.function.select.Selector;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.entity.domain.ReleaseActionDO;
import com.orion.ops.entity.domain.ReleaseBillDO;
import com.orion.ops.handler.release.action.*;
import com.orion.ops.handler.release.hint.ReleaseActionHint;
import com.orion.ops.handler.release.hint.ReleaseHint;
import com.orion.ops.handler.release.hint.ReleaseMachineHint;
import com.orion.ops.service.api.ReleaseInfoService;
import com.orion.utils.Strings;
import com.orion.utils.collect.Maps;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 上线单处理器工厂
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/15 17:17
 */
@Component
public class ReleaseProcessorFactory {

    @Resource
    private ReleaseInfoService releaseInfoService;

    /**
     * 创建上线单处理器
     *
     * @param releaseId releaseId
     * @return ReleaseProcessor
     */
    public ReleaseProcessor createReleaseProcessor(Long releaseId) {
        // 构建上线单信息
        ReleaseHint hint = this.buildReleaseHint(releaseId);
        // 构建上线单机器
        hint.setMachines(this.buildReleaseMachineHint(releaseId));
        // 查询action
        List<ReleaseActionDO> actions = releaseInfoService.getReleaseAction(releaseId);
        // 构建主机action
        List<IReleaseActionHandler> hostActionHandler = this.buildReleaseHostActionList(hint, actions);
        // 构建目标机器action

        // 处理器
        return new ReleaseProcessor(hint, hostActionHandler);
    }

    /**
     * 构建发布配置
     *
     * @param releaseId releaseId
     * @return ReleaseHint
     */
    private ReleaseHint buildReleaseHint(Long releaseId) {
        // 查询上线单信息
        ReleaseBillDO releaseBill = releaseInfoService.getReleaseBill(releaseId);
        // 封装
        ReleaseHint hint = new ReleaseHint();
        hint.setReleaseId(releaseBill.getId());
        hint.setTitle(releaseBill.getReleaseTitle());
        hint.setDescription(releaseBill.getReleaseDescription());
        hint.setAppName(releaseBill.getAppName());
        hint.setProfileName(releaseBill.getProfileName());
        hint.setType(releaseBill.getReleaseType());
        hint.setVcsLocalPath(releaseBill.getVcsLocalPath());
        hint.setBranchName(releaseBill.getBranchName());
        hint.setCommitId(releaseBill.getCommitId());
        hint.setDistPath(releaseBill.getDistPath());
        hint.setDistSnapshotPath(releaseBill.getDistSnapshotPath());
        hint.setReleaseUserId(releaseBill.getReleaseUserId());
        hint.setReleaseUserName(releaseBill.getReleaseUserName());
        String logPath = releaseBill.getLogPath();
        if (!Strings.isBlank(logPath)) {
            hint.setHostLogPath(MachineEnvAttr.LOG_PATH.getValue() + logPath);
        }
        hint.setSessionHolder(Maps.newMap());
        return hint;
    }

    /**
     * 构建机器配置
     *
     * @param releaseId releaseId
     * @return list
     */
    private List<ReleaseMachineHint> buildReleaseMachineHint(Long releaseId) {
        return releaseInfoService.getReleaseMachine(releaseId).stream()
                .map(s -> {
                    ReleaseMachineHint hint = new ReleaseMachineHint();
                    hint.setId(s.getId());
                    hint.setMachineId(s.getMachineId());
                    hint.setMachineHost(s.getMachineHost());
                    hint.setLogPath(hint.getLogPath());
                    hint.setDistPath(hint.getDistPath());
                    return hint;
                }).collect(Collectors.toList());
    }

    /**
     * 构建主机命令
     *
     * @param hint    hint
     * @param actions actions
     * @return list
     */
    private List<IReleaseActionHandler> buildReleaseHostActionList(ReleaseHint hint, List<ReleaseActionDO> actions) {
        return actions.stream()
                .filter(r -> ActionType.isHost(r.getActionType()))
                .map(a -> {
                    ReleaseActionHint actionHint = this.buildActionHint(a);
                    return Selector.<ActionType, IReleaseActionHandler>of(ActionType.of(a.getActionType(), false))
                            .test(Branches.eq(ActionType.CONNECT).then(s -> new ReleaseConnectActionHandler(hint, actionHint)))
                            .test(Branches.eq(ActionType.CHECKOUT).then(s -> new ReleaseCheckoutActionHandler(hint, actionHint)))
                            .test(Branches.eq(ActionType.TRANSFER).then(s -> new ReleaseTransferActionHandler(hint, actionHint)))
                            .test(Branches.eq(ActionType.HOST_COMMAND).then(s -> new ReleaseHostCommandActionHandler(hint, actionHint)))
                            .orElse(null);
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 构建步骤
     *
     * @param action action
     * @return ReleaseActionHint
     */
    private ReleaseActionHint buildActionHint(ReleaseActionDO action) {
        ReleaseActionHint hint = new ReleaseActionHint();
        hint.setId(action.getId());
        hint.setName(action.getActionName());
        hint.setType(action.getActionType());
        hint.setCommand(action.getActionCommand());
        String logPath = action.getLogPath();
        if (!Strings.isBlank(logPath)) {
            hint.setLogPath(MachineEnvAttr.LOG_PATH.getValue() + logPath);
        }
        return hint;
    }

}
