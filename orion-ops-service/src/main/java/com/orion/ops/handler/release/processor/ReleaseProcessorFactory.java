package com.orion.ops.handler.release.processor;

import com.orion.function.select.Branches;
import com.orion.function.select.Selector;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.consts.app.ReleaseType;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.entity.domain.ReleaseActionDO;
import com.orion.ops.entity.domain.ReleaseBillDO;
import com.orion.ops.handler.release.action.*;
import com.orion.ops.handler.release.hint.ReleaseActionHint;
import com.orion.ops.handler.release.hint.ReleaseHint;
import com.orion.ops.handler.release.hint.ReleaseMachineHint;
import com.orion.ops.service.api.ReleaseInfoService;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.Valid;
import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Maps;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    public IReleaseProcessor createReleaseProcessor(Long releaseId) {
        // 构建上线单信息
        ReleaseHint hint = this.buildReleaseHint(releaseId);
        // 构建上线单机器
        List<ReleaseMachineHint> machines = this.buildReleaseMachineHint(releaseId);
        hint.setMachines(machines);
        // 查询action
        List<ReleaseActionDO> actions = releaseInfoService.getReleaseAction(releaseId);
        // 构建主机action
        List<IReleaseActionHandler> hostActionHandler = this.buildReleaseHostActionList(hint, actions);
        // 构建目标机器action
        List<ReleaseTargetStageHandler> targetStages = this.buildReleaseTargetActionList(hint, machines, actions);
        // 处理器选择
        return Selector.<ReleaseType, IReleaseProcessor>of(ReleaseType.of(hint.getType()))
                .test(Branches.eq(ReleaseType.NORMAL).then(s -> new NormalReleaseProcessor(hint, hostActionHandler, targetStages)))
                .test(Branches.eq(ReleaseType.ROLLBACK).then(s -> new RollbackReleaseProcessor(hint, hostActionHandler, targetStages)))
                .orThrow(() -> Exceptions.argument(MessageConst.UNKNOWN_RELEASE_TYPE));
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
        hint.setRollbackReleaseId(releaseBill.getRollbackReleaseId());
        hint.setTitle(releaseBill.getReleaseTitle());
        hint.setDescription(releaseBill.getReleaseDescription());
        hint.setAppId(releaseBill.getAppId());
        hint.setAppName(releaseBill.getAppName());
        hint.setProfileId(releaseBill.getProfileId());
        hint.setProfileName(releaseBill.getProfileName());
        hint.setType(releaseBill.getReleaseType());
        hint.setVcsLocalPath(releaseBill.getVcsLocalPath());
        hint.setBranchName(releaseBill.getBranchName());
        hint.setCommitId(releaseBill.getCommitId());
        hint.setDistPath(releaseBill.getDistPath());
        hint.setDistSnapshotPath(MachineEnvAttr.DIST_PATH.getValue() + releaseBill.getDistSnapshotPath());
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
                    hint.setLogPath(MachineEnvAttr.LOG_PATH.getValue() + s.getLogPath());
                    hint.setDistPath(s.getDistPath());
                    return hint;
                }).collect(Collectors.toList());
    }

    /**
     * 构建宿主机操作
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
                            .test(Branches.eq(ActionType.HOST_COMMAND).then(s -> new ReleaseCommandActionHandler(hint, actionHint)))
                            .orElse(null);
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 构建目标机器操作
     *
     * @param hint    hint
     * @param actions actions
     * @return list
     */
    private List<ReleaseTargetStageHandler> buildReleaseTargetActionList(ReleaseHint hint, List<ReleaseMachineHint> machines, List<ReleaseActionDO> actions) {
        Map<Long, List<ReleaseActionDO>> machineActions = actions.stream()
                .filter(r -> !ActionType.isHost(r.getActionType()))
                .collect(Collectors.groupingBy(ReleaseActionDO::getMachineId));
        if (machineActions.isEmpty()) {
            return Lists.empty();
        }
        // 目标机器stage
        return machineActions.entrySet().stream()
                .map(e -> this.buildTargetActionStage(hint, machines, e.getKey(), e.getValue()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 构建 targetActionStage
     *
     * @param hint           hint
     * @param machines       machines
     * @param machineId      machineId
     * @param machineActions machineActions
     * @return ReleaseTargetStageHandler
     */
    private ReleaseTargetStageHandler buildTargetActionStage(ReleaseHint hint, List<ReleaseMachineHint> machines, Long machineId, List<ReleaseActionDO> machineActions) {
        // 机器配置
        Optional<ReleaseMachineHint> machine = machines.stream()
                .filter(m -> m.getMachineId().equals(machineId))
                .findFirst();
        Valid.isTrue(machine.isPresent());
        ReleaseMachineHint machineHint = machine.get();
        // action
        List<IReleaseActionHandler> actions = machineActions.stream()
                .map(a -> {
                    ReleaseActionHint actionHint = this.buildActionHint(a);
                    return Selector.<ActionType, IReleaseActionHandler>of(ActionType.of(a.getActionType(), false))
                            .test(Branches.eq(ActionType.TARGET_COMMAND).then(s -> new ReleaseTargetCommandActionHandler(hint, machineHint, actionHint)))
                            .orElse(null);
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (actions.isEmpty()) {
            return null;
        }
        return new ReleaseTargetStageHandler(hint, machineHint, actions);
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
