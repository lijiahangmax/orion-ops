package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.*;
import com.orion.ops.consts.app.*;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.ApplicationReleaseAuditRequest;
import com.orion.ops.entity.request.ApplicationReleaseRequest;
import com.orion.ops.entity.vo.*;
import com.orion.ops.handler.release.IReleaseProcessor;
import com.orion.ops.handler.release.ReleaseSessionHolder;
import com.orion.ops.service.api.*;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Maps;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.Files1;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 发布单 服务实现类
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
@Service("applicationReleaseService")
public class ApplicationReleaseServiceImpl implements ApplicationReleaseService {

    @Resource
    private ApplicationReleaseDAO applicationReleaseDAO;

    @Resource
    private ApplicationReleaseMachineService applicationReleaseMachineService;

    @Resource
    private ApplicationReleaseMachineDAO applicationReleaseMachineDAO;

    @Resource
    private ApplicationReleaseActionService applicationReleaseActionService;

    @Resource
    private ApplicationReleaseActionDAO applicationReleaseActionDAO;

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Resource
    private ApplicationProfileDAO applicationProfileDAO;

    @Resource
    private ApplicationActionService applicationActionService;

    @Resource
    private ApplicationEnvService applicationEnvService;

    @Resource
    private ApplicationMachineService applicationMachineService;

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private MachineEnvService machineEnvService;

    @Resource
    private ReleaseSessionHolder releaseSessionHolder;

    @Override
    public DataGrid<ApplicationReleaseListVO> getReleaseList(ApplicationReleaseRequest request) {
        Long userId = Currents.getUserId();
        LambdaQueryWrapper<ApplicationReleaseDO> wrapper = new LambdaQueryWrapper<ApplicationReleaseDO>()
                .like(!Strings.isBlank(request.getTitle()), ApplicationReleaseDO::getReleaseTitle, request.getTitle())
                .like(!Strings.isBlank(request.getDescription()), ApplicationReleaseDO::getReleaseDescription, request.getDescription())
                .eq(Objects.nonNull(request.getAppId()), ApplicationReleaseDO::getAppId, request.getAppId())
                .eq(Objects.nonNull(request.getProfileId()), ApplicationReleaseDO::getProfileId, request.getProfileId())
                .eq(Objects.nonNull(request.getStatus()), ApplicationReleaseDO::getReleaseStatus, request.getStatus())
                .and(Const.ENABLE.equals(request.getOnlyMyself()), w -> w
                        .eq(ApplicationReleaseDO::getCreateUserId, userId)
                        .or()
                        .eq(ApplicationReleaseDO::getReleaseUserId, userId))
                .orderByDesc(ApplicationReleaseDO::getId);
        // 查询列表
        DataGrid<ApplicationReleaseListVO> dataGrid = DataQuery.of(applicationReleaseDAO)
                .wrapper(wrapper)
                .page(request)
                .dataGrid(ApplicationReleaseListVO.class);
        if (Const.ENABLE.equals(request.getQueryMachine())) {
            // 查询发布机器
            List<Long> machineIdList = dataGrid.stream().map(ApplicationReleaseListVO::getId).collect(Collectors.toList());
            if (!machineIdList.isEmpty()) {
                // 查询机器
                Map<Long, List<ApplicationReleaseMachineVO>> releaseMachineMap = applicationReleaseMachineService.getReleaseMachines(machineIdList)
                        .stream()
                        .map(s -> Converts.to(s, ApplicationReleaseMachineVO.class))
                        .collect(Collectors.groupingBy(ApplicationReleaseMachineVO::getReleaseId));
                dataGrid.forEach(release -> release.setMachines(releaseMachineMap.get(release.getId())));
            }
        }
        return dataGrid;
    }

    @Override
    public List<ApplicationReleaseMachineVO> getReleaseMachineList(Long id) {
        List<ApplicationReleaseMachineDO> machines = applicationReleaseMachineService.getReleaseMachines(id);
        return Converts.toList(machines, ApplicationReleaseMachineVO.class);
    }

    @Override
    public ApplicationReleaseDetailVO getReleaseDetail(Long id) {
        // 查询
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(id);
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        ApplicationReleaseDetailVO vo = Converts.to(release, ApplicationReleaseDetailVO.class);
        // 设置action
        List<ApplicationActionDO> actions = JSON.parseArray(release.getActionConfig(), ApplicationActionDO.class);
        vo.setActions(Converts.toList(actions, ApplicationActionVO.class));
        // 查询机器
        List<ApplicationReleaseMachineVO> machines = applicationReleaseMachineService.getReleaseMachines(id).stream()
                .map(s -> Converts.to(s, ApplicationReleaseMachineVO.class))
                .collect(Collectors.toList());
        vo.setMachines(machines);
        return vo;
    }

    @Override
    public ApplicationReleaseMachineVO getReleaseMachineDetail(Long releaseMachineId) {
        // 查询数据
        ApplicationReleaseMachineDO machine = applicationReleaseMachineDAO.selectById(releaseMachineId);
        Valid.notNull(machine, MessageConst.RELEASE_ABSENT);
        ApplicationReleaseMachineVO vo = Converts.to(machine, ApplicationReleaseMachineVO.class);
        // 查询action
        List<ApplicationReleaseActionVO> actions = applicationReleaseActionService.getReleaseActionByReleaseMachineId(releaseMachineId).stream()
                .map(s -> Converts.to(s, ApplicationReleaseActionVO.class))
                .collect(Collectors.toList());
        vo.setActions(actions);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitAppRelease(ApplicationReleaseRequest request) {
        Long appId = request.getAppId();
        Long profileId = request.getProfileId();
        // 查询应用
        ApplicationInfoDO app = applicationInfoDAO.selectById(appId);
        Valid.notNull(app, MessageConst.APP_ABSENT);
        // 查询环境
        ApplicationProfileDO profile = applicationProfileDAO.selectById(profileId);
        Valid.notNull(profile, MessageConst.PROFILE_ABSENT);
        // 查询应用执行块
        List<ApplicationActionDO> actions = applicationActionService.getAppProfileActions(appId, profileId, StageType.RELEASE.getType());
        Valid.notEmpty(actions, MessageConst.APP_PROFILE_NOT_CONFIGURED);
        // 检查发布机器
        this.checkReleaseMachine(request);
        // 查询机器
        Map<Long, MachineInfoDO> machines = this.getReleaseMachineInfo(request);
        // 设置主表信息
        ApplicationReleaseDO release = this.setReleaseInfo(request, app, profile, actions);
        applicationReleaseDAO.insert(release);
        Long releaseId = release.getId();
        // 设置机器信息
        List<ApplicationReleaseMachineDO> releaseMachines = this.setReleaseMachineInfo(request, machines, releaseId);
        releaseMachines.forEach(applicationReleaseMachineDAO::insert);
        // 检查是否包含命令
        boolean hasCommand = actions.stream()
                .map(ApplicationActionDO::getActionType)
                .anyMatch(ActionType.RELEASE_COMMAND.getType()::equals);
        Map<String, String> releaseEnv = Maps.newMap();
        if (hasCommand) {
            // 查询应用环境变量
            releaseEnv.putAll(applicationEnvService.getAppProfileFullEnv(appId, profileId));
            // 添加发布环境变量
            releaseEnv.putAll(this.getReleaseEnv(release));
        }
        // 设置部署操作
        List<ApplicationReleaseActionDO> releaseActions = this.setReleaseActions(actions, releaseMachines, releaseEnv);
        releaseActions.forEach(applicationReleaseActionDAO::insert);
        return releaseId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer auditAppRelease(ApplicationReleaseAuditRequest request) {
        // 查询状态
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(request.getId());
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        ReleaseStatus status = ReleaseStatus.of(release.getReleaseStatus());
        if (!ReleaseStatus.WAIT_AUDIT.equals(status) && !ReleaseStatus.AUDIT_REJECT.equals(status)) {
            throw Exceptions.argument(MessageConst.RELEASE_ILLEGAL_STATUS);
        }
        AuditStatus auditStatus = AuditStatus.of(request.getStatus());
        UserDTO user = Currents.getUser();
        // 更新
        ApplicationReleaseDO update = new ApplicationReleaseDO();
        update.setId(request.getId());
        update.setAuditUserId(user.getId());
        update.setAuditUserName(user.getUsername());
        update.setAuditTime(new Date());
        update.setAuditReason(request.getReason());
        if (AuditStatus.RESOLVE.equals(auditStatus)) {
            // 通过
            update.setReleaseStatus(ReleaseStatus.WAIT_RUNNABLE.getStatus());
        } else if (AuditStatus.REJECT.equals(auditStatus)) {
            // 驳回
            update.setReleaseStatus(ReleaseStatus.AUDIT_REJECT.getStatus());
        } else {
            return 0;
        }
        return applicationReleaseDAO.updateById(update);
    }

    @Override
    public void runnableAppRelease(Long id) {
        // 查询状态
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(id);
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        ReleaseStatus status = ReleaseStatus.of(release.getReleaseStatus());
        if (!ReleaseStatus.WAIT_RUNNABLE.equals(status)) {
            throw Exceptions.argument(MessageConst.RELEASE_ILLEGAL_STATUS);
        }
        UserDTO user = Currents.getUser();
        // 更新发布人
        ApplicationReleaseDO update = new ApplicationReleaseDO();
        update.setId(id);
        update.setReleaseUserId(user.getId());
        update.setReleaseUserName(user.getUsername());
        applicationReleaseDAO.updateById(update);
        // 发布
        IReleaseProcessor.with(release).exec();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long rollbackAppRelease(Long id) {
        // 查询状态
        ApplicationReleaseDO rollback = applicationReleaseDAO.selectById(id);
        Valid.notNull(rollback, MessageConst.RELEASE_ABSENT);
        ReleaseStatus status = ReleaseStatus.of(rollback.getReleaseStatus());
        if (!ReleaseStatus.FINISH.equals(status)) {
            throw Exceptions.argument(MessageConst.RELEASE_ILLEGAL_STATUS);
        }
        // 检查产物是否存在
        String path = Files1.getPath(MachineEnvAttr.DIST_PATH.getValue(), rollback.getBundlePath());
        if (!new File(path).exists()) {
            throw Exceptions.argument(MessageConst.FILE_ABSENT_UNABLE_ROLLBACK);
        }
        // 查询环境
        ApplicationProfileDO profile = applicationProfileDAO.selectById(rollback.getProfileId());
        Valid.notNull(profile, MessageConst.PROFILE_ABSENT);
        // 设置主表信息
        this.setRollbackReleaseInfo(rollback, profile);
        applicationReleaseDAO.insert(rollback);
        Long releaseId = rollback.getId();
        // 设置机器信息
        List<ApplicationReleaseMachineDO> machines = applicationReleaseMachineService.getReleaseMachines(id);
        for (ApplicationReleaseMachineDO machine : machines) {
            Long beforeId = machine.getId();
            machine.setId(null);
            machine.setReleaseId(releaseId);
            machine.setRunStatus(ActionStatus.WAIT.getStatus());
            machine.setLogPath(PathBuilders.getReleaseMachineLogPath(releaseId, machine.getMachineId()));
            machine.setStartTime(null);
            machine.setEndTime(null);
            machine.setCreateTime(null);
            machine.setUpdateTime(null);
            applicationReleaseMachineDAO.insert(machine);
            // 设置操作信息
            List<ApplicationReleaseActionDO> actions = applicationReleaseActionService.getReleaseActionByReleaseMachineId(beforeId);
            for (ApplicationReleaseActionDO action : actions) {
                action.setId(null);
                action.setReleaseMachineId(machine.getId());
                action.setMachineId(machine.getMachineId());
                action.setReleaseId(releaseId);
                action.setLogPath(PathBuilders.getReleaseActionLogPath(releaseId, machine.getMachineId(), action.getActionId()));
                action.setRunStatus(ActionStatus.WAIT.getStatus());
                action.setExitCode(null);
                action.setStartTime(null);
                action.setEndTime(null);
                action.setCreateTime(null);
                action.setUpdateTime(null);
                applicationReleaseActionDAO.insert(action);
            }
        }
        return releaseId;
    }

    @Override
    public void terminatedRelease(Long id) {
        // 获取数据
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(id);
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        // 检查状态
        ApplicationReleaseDO update;
        switch (ReleaseStatus.of(release.getReleaseStatus())) {
            case WAIT_RUNNABLE:
            case RUNNABLE:
                // 获取实例
                IReleaseProcessor session = releaseSessionHolder.getSession(id);
                if (session == null) {
                    update = new ApplicationReleaseDO();
                    update.setId(id);
                    update.setReleaseStatus(ReleaseStatus.TERMINATED.getStatus());
                    update.setReleaseEndTime(new Date());
                    applicationReleaseDAO.updateById(update);
                } else {
                    // 调用终止
                    session.terminated();
                }
                break;
            default:
                break;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteRelease(Long id) {
        // 查询状态
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(id);
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        if (ReleaseStatus.RUNNABLE.getStatus().equals(release.getReleaseStatus())) {
            throw Exceptions.argument(MessageConst.RELEASE_ILLEGAL_STATUS);
        }
        // 删除主表
        int effect = applicationReleaseDAO.deleteById(id);
        // 删除机器
        Wrapper<ApplicationReleaseMachineDO> machineWrapper = new LambdaQueryWrapper<ApplicationReleaseMachineDO>()
                .eq(ApplicationReleaseMachineDO::getReleaseId, id);
        effect += applicationReleaseMachineDAO.delete(machineWrapper);
        // 删除操作
        Wrapper<ApplicationReleaseActionDO> actionWrapper = new LambdaQueryWrapper<ApplicationReleaseActionDO>()
                .eq(ApplicationReleaseActionDO::getReleaseId, id);
        effect += applicationReleaseActionDAO.delete(actionWrapper);
        return effect;
    }

    @Override
    public List<ApplicationReleaseStatusVO> getReleaseStatusList(List<Long> idList, List<Long> machineIdList) {
        // 查询发布状态
        List<ApplicationReleaseDO> releaseList = applicationReleaseDAO.selectStatusByIdList(idList);
        List<ApplicationReleaseStatusVO> statusList = Converts.toList(releaseList, ApplicationReleaseStatusVO.class);
        // 查询机器状态
        if (Lists.isNotEmpty(machineIdList)) {
            List<ApplicationReleaseMachineDO> machineList = applicationReleaseMachineDAO.selectStatusByIdList(machineIdList);
            for (ApplicationReleaseStatusVO status : statusList) {
                List<ApplicationReleaseMachineDO> machineStatus = machineList.stream()
                        .filter(s -> s.getReleaseId().equals(status.getId()))
                        .collect(Collectors.toList());
                status.setMachines(Converts.toList(machineStatus, ApplicationReleaseMachineStatusVO.class));
            }
        }
        return statusList;
    }

    @Override
    public ApplicationReleaseStatusVO getReleaseStatus(Long id) {
        // 查询发布状态
        ApplicationReleaseDO release = applicationReleaseDAO.selectStatusById(id);
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        ApplicationReleaseStatusVO status = Converts.to(release, ApplicationReleaseStatusVO.class);
        // 查询机器状态
        List<ApplicationReleaseMachineDO> machines = applicationReleaseMachineDAO.selectStatusByReleaseId(id);
        List<ApplicationReleaseMachineStatusVO> machineStatus = Converts.toList(machines, ApplicationReleaseMachineStatusVO.class);
        status.setMachines(machineStatus);
        return status;
    }

    @Override
    public List<ApplicationReleaseMachineStatusVO> getReleaseMachineStatusList(List<Long> releaseMachineIdList) {
        // 查询机器状态
        List<ApplicationReleaseMachineDO> machines = applicationReleaseMachineDAO.selectStatusByIdList(releaseMachineIdList);
        List<ApplicationReleaseMachineStatusVO> machinesStatus = Converts.toList(machines, ApplicationReleaseMachineStatusVO.class);
        // 查询操作状态
        if (!machinesStatus.isEmpty()) {
            List<ApplicationReleaseActionDO> actions = applicationReleaseActionDAO.selectReleaseActionStatusByMachineIdList(releaseMachineIdList);
            for (ApplicationReleaseMachineStatusVO machineStatus : machinesStatus) {
                List<ApplicationReleaseActionDO> machineActions = actions.stream()
                        .filter(s -> s.getReleaseMachineId().equals(machineStatus.getId()))
                        .collect(Collectors.toList());
                machineStatus.setActions(Converts.toList(machineActions, ApplicationReleaseActionStatusVO.class));
            }
        }
        return machinesStatus;
    }

    @Override
    public ApplicationReleaseMachineStatusVO getReleaseMachineStatus(Long releaseMachineId) {
        // 查询机器状态
        ApplicationReleaseMachineDO machine = applicationReleaseMachineDAO.selectStatusById(releaseMachineId);
        Valid.notNull(machine, MessageConst.UNKNOWN_DATA);
        ApplicationReleaseMachineStatusVO status = Converts.to(machine, ApplicationReleaseMachineStatusVO.class);
        // 查询操作状态
        List<ApplicationReleaseActionDO> actions = applicationReleaseActionDAO.selectReleaseActionStatusByMachineId(releaseMachineId);
        List<ApplicationReleaseActionStatusVO> actionStatus = Converts.toList(actions, ApplicationReleaseActionStatusVO.class);
        status.setActions(actionStatus);
        return status;
    }

    /**
     * 检查发布机器
     *
     * @param request request
     */
    private void checkReleaseMachine(ApplicationReleaseRequest request) {
        // 获取发布配置机器
        List<Long> machineIdList = applicationMachineService.getAppProfileMachineIdList(request.getAppId(), request.getProfileId(), false);
        Valid.notEmpty(machineIdList, MessageConst.UNABLE_CONFIG_RELEASE_MACHINE);
        for (Long machineId : request.getMachineIdList()) {
            // 检查发布机器是否在配置中
            if (machineIdList.stream().noneMatch(machineId::equals)) {
                throw Exceptions.argument(MessageConst.UNKNOWN_RELEASE_MACHINE);
            }
        }
    }

    /**
     * 查询发布机器信息
     *
     * @param request request
     * @return machine
     */
    private Map<Long, MachineInfoDO> getReleaseMachineInfo(ApplicationReleaseRequest request) {
        Map<Long, MachineInfoDO> map = Maps.newMap();
        for (Long machineId : request.getMachineIdList()) {
            MachineInfoDO machine = machineInfoService.selectById(machineId);
            Valid.notNull(machine, MessageConst.INVALID_MACHINE);
            map.put(machineId, machine);
        }
        return map;
    }

    /**
     * 设置发布参数
     *
     * @param request request
     * @param app     app
     * @param profile profile
     * @param actions actions
     * @return releaseInfo
     */
    private ApplicationReleaseDO setReleaseInfo(ApplicationReleaseRequest request,
                                                ApplicationInfoDO app,
                                                ApplicationProfileDO profile,
                                                List<ApplicationActionDO> actions) {
        UserDTO user = Currents.getUser();
        // 查询构建产物路径
        ApplicationBuildDO build = applicationBuildDAO.selectById(request.getBuildId());
        Valid.notNull(build, MessageConst.BUILD_ABSENT);
        String buildBundlePath = this.getBuildBundlePath(build);
        // 查询产物传输路径
        String transferPath = applicationEnvService.getAppEnvValue(app.getId(), profile.getId(), ApplicationEnvAttr.TRANSFER_PATH.getKey());
        // 查询发布序列
        String releaseSerial = applicationEnvService.getAppEnvValue(app.getId(), profile.getId(), ApplicationEnvAttr.RELEASE_SERIAL.getKey());
        // 设置参数
        ApplicationReleaseDO release = new ApplicationReleaseDO();
        release.setReleaseTitle(request.getTitle());
        release.setReleaseDescription(request.getDescription());
        release.setBuildId(build.getId());
        release.setBuildSeq(build.getBuildSeq());
        release.setAppId(app.getId());
        release.setAppName(app.getAppName());
        release.setAppTag(app.getAppTag());
        release.setProfileId(profile.getId());
        release.setProfileName(profile.getProfileName());
        release.setProfileTag(profile.getProfileTag());
        release.setReleaseType(ReleaseType.NORMAL.getType());
        release.setReleaseSerialize(ReleaseSerialType.of(releaseSerial).getType());
        release.setBundlePath(buildBundlePath);
        release.setTransferPath(transferPath);
        release.setTimedRelease(request.getTimedRelease());
        release.setTimedReleaseTime(request.getTimedReleaseTime());
        release.setCreateUserId(user.getId());
        release.setCreateUserName(user.getUsername());
        release.setActionConfig(JSON.toJSONString(actions));
        // 设置审核信息
        this.setCreateAuditInfo(release, user, Const.ENABLE.equals(profile.getReleaseAudit()));
        return release;
    }

    /**
     * 设置回滚发布信息
     *
     * @param rollback rollback
     * @param profile  profile
     */
    private void setRollbackReleaseInfo(ApplicationReleaseDO rollback, ApplicationProfileDO profile) {
        UserDTO user = Currents.getUser();
        rollback.setId(null);
        rollback.setReleaseTitle(rollback.getReleaseTitle() + " - " + Const.ROLLBACK);
        rollback.setReleaseType(ReleaseType.ROLLBACK.getType());
        rollback.setRollbackReleaseId(rollback.getId());
        rollback.setTimedRelease(TimedReleaseType.NORMAL.getType());
        rollback.setTimedReleaseTime(null);
        rollback.setCreateUserId(user.getId());
        rollback.setCreateUserName(user.getUsername());
        rollback.setAuditUserId(null);
        rollback.setAuditUserName(null);
        rollback.setAuditTime(null);
        rollback.setAuditReason(null);
        rollback.setReleaseStartTime(null);
        rollback.setReleaseEndTime(null);
        rollback.setReleaseUserId(null);
        rollback.setReleaseUserName(null);
        rollback.setCreateTime(null);
        rollback.setUpdateTime(null);
        this.setCreateAuditInfo(rollback, user, Const.ENABLE.equals(profile.getReleaseAudit()));
    }

    /**
     * 设置发布机器参数
     *
     * @param request   request
     * @param machines  machines
     * @param releaseId releaseId
     * @return machine
     */
    private List<ApplicationReleaseMachineDO> setReleaseMachineInfo(ApplicationReleaseRequest request, Map<Long, MachineInfoDO> machines, Long releaseId) {
        return request.getMachineIdList().stream().map(machineId -> {
            ApplicationReleaseMachineDO machine = new ApplicationReleaseMachineDO();
            machine.setReleaseId(releaseId);
            machine.setMachineId(machineId);
            MachineInfoDO machineInfo = machines.get(machineId);
            machine.setMachineName(machineInfo.getMachineName());
            machine.setMachineTag(machineInfo.getMachineTag());
            machine.setMachineHost(machineInfo.getMachineHost());
            machine.setRunStatus(ActionStatus.WAIT.getStatus());
            machine.setLogPath(PathBuilders.getReleaseMachineLogPath(releaseId, machineId));
            return machine;
        }).collect(Collectors.toList());
    }

    /**
     * 设置发布部署操作
     *
     * @param actions         actions
     * @param releaseMachines 发布机器
     * @param releaseEnv      发布环境变量
     * @return actions
     */
    private List<ApplicationReleaseActionDO> setReleaseActions(List<ApplicationActionDO> actions, List<ApplicationReleaseMachineDO> releaseMachines, Map<String, String> releaseEnv) {
        List<ApplicationReleaseActionDO> releaseActions = Lists.newList();
        for (ApplicationReleaseMachineDO releaseMachine : releaseMachines) {
            for (ApplicationActionDO action : actions) {
                ActionType actionType = ActionType.of(action.getActionType());
                Long machineId = releaseMachine.getMachineId();
                ApplicationReleaseActionDO releaseAction = new ApplicationReleaseActionDO();
                releaseAction.setReleaseMachineId(releaseMachine.getId());
                releaseAction.setMachineId(machineId);
                Long releaseId = releaseMachine.getReleaseId();
                releaseAction.setReleaseId(releaseId);
                releaseAction.setActionId(action.getId());
                releaseAction.setActionName(action.getActionName());
                releaseAction.setActionType(action.getActionType());
                if (ActionType.RELEASE_COMMAND.equals(actionType)) {
                    String command = action.getActionCommand();
                    // 替换发布命令
                    command = Strings.format(command, EnvConst.SYMBOL, releaseEnv);
                    // 替换机器命令
                    Map<String, String> machineEnv = machineEnvService.getFullMachineEnv(machineId);
                    command = Strings.format(command, EnvConst.SYMBOL, machineEnv);
                    releaseAction.setActionCommand(command);
                }
                releaseAction.setLogPath(PathBuilders.getReleaseActionLogPath(releaseId, machineId, action.getId()));
                releaseAction.setRunStatus(ActionStatus.WAIT.getStatus());
                releaseActions.add(releaseAction);
            }
        }
        return releaseActions;
    }

    /**
     * 检查并且获取构建目录
     *
     * @param build build
     * @return 构建产物路径
     */
    private String getBuildBundlePath(ApplicationBuildDO build) {
        // 构建产物
        String buildBundlePath = build.getBundlePath();
        File bundleFile = new File(Files1.getPath(MachineEnvAttr.DIST_PATH.getValue(), buildBundlePath));
        Valid.isTrue(bundleFile.exists(), MessageConst.BUNDLE_FILE_ABSENT);
        if (bundleFile.isFile()) {
            return buildBundlePath;
        }
        // 如果是文件夹则需要检查传输文件是文件夹还是压缩文件
        String transferDirValue = applicationEnvService.getAppEnvValue(build.getAppId(), build.getProfileId(), ApplicationEnvAttr.TRANSFER_DIR_TYPE.getKey());
        if (TransferDirType.ZIP.equals(TransferDirType.of(transferDirValue))) {
            buildBundlePath = build.getBundlePath() + "." + Const.SUFFIX_ZIP;
            bundleFile = new File(Files1.getPath(MachineEnvAttr.DIST_PATH.getValue(), buildBundlePath));
            Valid.isTrue(bundleFile.exists(), MessageConst.BUNDLE_ZIP_FILE_ABSENT);
        }
        return buildBundlePath;
    }

    /**
     * 创建时设置是否需要审核
     *
     * @param release   release
     * @param user      user
     * @param needAudit needAudit
     */
    private void setCreateAuditInfo(ApplicationReleaseDO release, UserDTO user, boolean needAudit) {
        boolean isAdmin = RoleType.isAdministrator(user.getRoleType());
        // 需要审核 & 不是管理员
        if (needAudit && !isAdmin) {
            release.setReleaseStatus(ReleaseStatus.WAIT_AUDIT.getStatus());
        } else {
            release.setReleaseStatus(ReleaseStatus.WAIT_RUNNABLE.getStatus());
            release.setAuditUserId(user.getId());
            release.setAuditUserName(user.getUsername());
            release.setAuditTime(new Date());
            if (isAdmin) {
                release.setAuditReason(MessageConst.AUTO_AUDIT_RESOLVE);
            } else {
                release.setAuditReason(MessageConst.AUDIT_NO_REQUIRED);
            }
        }
    }

    /**
     * 获取发布环境变量
     *
     * @param release release
     * @return env
     */
    private Map<String, String> getReleaseEnv(ApplicationReleaseDO release) {
        // 设置变量
        MutableLinkedHashMap<String, String> env = Maps.newMutableLinkedMap();
        env.put(EnvConst.BUILD_ID, release.getBuildId() + Const.EMPTY);
        env.put(EnvConst.BUILD_SEQ, release.getBuildSeq() + Const.EMPTY);
        env.put(EnvConst.RELEASE_ID, release.getId() + Const.EMPTY);
        env.put(EnvConst.RELEASE_TITLE, release.getReleaseTitle());
        // 设置前缀
        MutableLinkedHashMap<String, String> fullEnv = Maps.newMutableLinkedMap();
        env.forEach((k, v) -> {
            fullEnv.put(EnvConst.RELEASE_PREFIX + k, v);
        });
        return fullEnv;
    }

}
