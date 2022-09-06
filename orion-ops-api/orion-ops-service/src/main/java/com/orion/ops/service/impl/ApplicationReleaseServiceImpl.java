package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.collect.MutableLinkedHashMap;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.constant.*;
import com.orion.ops.constant.app.*;
import com.orion.ops.constant.env.EnvConst;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.message.MessageType;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.constant.user.RoleType;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.user.UserDTO;
import com.orion.ops.entity.request.app.ApplicationReleaseAuditRequest;
import com.orion.ops.entity.request.app.ApplicationReleaseRequest;
import com.orion.ops.entity.vo.app.*;
import com.orion.ops.handler.app.release.IReleaseProcessor;
import com.orion.ops.handler.app.release.ReleaseSessionHolder;
import com.orion.ops.service.api.*;
import com.orion.ops.task.TaskRegister;
import com.orion.ops.task.TaskType;
import com.orion.ops.utils.*;
import com.orion.spring.SpringHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 发布任务 服务实现类
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
    private ApplicationActionLogService applicationActionLogService;

    @Resource
    private ApplicationActionLogDAO applicationActionLogDAO;

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
    private ApplicationBuildService applicationBuildService;

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private MachineEnvService machineEnvService;

    @Resource
    private SystemEnvService systemEnvService;

    @Resource
    private WebSideMessageService webSideMessageService;

    @Resource
    private ReleaseSessionHolder releaseSessionHolder;

    @Resource
    private TaskRegister taskRegister;

    @Override
    public DataGrid<ApplicationReleaseListVO> getReleaseList(ApplicationReleaseRequest request) {
        Long userId = Currents.getUserId();
        LambdaQueryWrapper<ApplicationReleaseDO> wrapper = new LambdaQueryWrapper<ApplicationReleaseDO>()
                .like(!Strings.isBlank(request.getTitle()), ApplicationReleaseDO::getReleaseTitle, request.getTitle())
                .like(!Strings.isBlank(request.getDescription()), ApplicationReleaseDO::getReleaseDescription, request.getDescription())
                .like(!Strings.isBlank(request.getAppName()), ApplicationReleaseDO::getAppName, request.getAppName())
                .eq(Objects.nonNull(request.getId()), ApplicationReleaseDO::getId, request.getId())
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
                .page(request)
                .wrapper(wrapper)
                .dataGrid(ApplicationReleaseListVO.class);
        if (Const.ENABLE.equals(request.getQueryMachine())) {
            // 查询发布机器
            List<Long> machineIdList = dataGrid.stream()
                    .map(ApplicationReleaseListVO::getId)
                    .collect(Collectors.toList());
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
    public ApplicationReleaseDetailVO getReleaseDetail(ApplicationReleaseRequest request) {
        // 查询
        Long id = request.getId();
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
        // 查询操作
        if (Const.ENABLE.equals(request.getQueryAction())) {
            List<Long> releaseMachineIdList = machines.stream().map(ApplicationReleaseMachineVO::getId).collect(Collectors.toList());
            if (!releaseMachineIdList.isEmpty()) {
                // 机器操作
                List<ApplicationActionLogDO> machineActions = applicationActionLogService.selectActionByRelIdList(releaseMachineIdList, StageType.RELEASE);
                Map<Long, List<ApplicationActionLogVO>> machineActionsMap = machineActions.stream()
                        .map(s -> Converts.to(s, ApplicationActionLogVO.class))
                        .collect(Collectors.groupingBy(ApplicationActionLogVO::getRelId));
                for (ApplicationReleaseMachineVO machine : machines) {
                    machine.setActions(machineActionsMap.get(machine.getId()));
                }
            }
        }
        return vo;
    }

    @Override
    public ApplicationReleaseMachineVO getReleaseMachineDetail(Long releaseMachineId) {
        // 查询数据
        ApplicationReleaseMachineDO machine = applicationReleaseMachineDAO.selectById(releaseMachineId);
        Valid.notNull(machine, MessageConst.RELEASE_ABSENT);
        ApplicationReleaseMachineVO vo = Converts.to(machine, ApplicationReleaseMachineVO.class);
        // 查询action
        List<ApplicationActionLogVO> actions = applicationActionLogService.getActionLogsByRelId(releaseMachineId, StageType.RELEASE);
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
        // 查询构建信息
        ApplicationBuildDO build = applicationBuildDAO.selectById(request.getBuildId());
        Valid.notNull(build, MessageConst.BUILD_ABSENT);
        // 设置主表信息
        ApplicationReleaseDO release = this.setReleaseInfo(request, app, profile, build, actions);
        applicationReleaseDAO.insert(release);
        Long releaseId = release.getId();
        // 设置机器信息
        List<ApplicationReleaseMachineDO> releaseMachines = this.setReleaseMachineInfo(request, machines, releaseId);
        releaseMachines.forEach(applicationReleaseMachineDAO::insert);
        // 检查是否包含命令
        final boolean hasEnvCommand = actions.stream()
                .filter(s -> ActionType.RELEASE_COMMAND.getType().equals(s.getActionType()))
                .map(ApplicationActionDO::getActionCommand)
                .filter(Strings::isNotBlank)
                .anyMatch(s -> s.contains(EnvConst.SYMBOL));
        Map<String, String> releaseEnv = Maps.newMap();
        if (hasEnvCommand) {
            // 查询应用环境变量
            releaseEnv.putAll(applicationEnvService.getAppProfileFullEnv(appId, profileId));
            // 添加系统环境变量
            releaseEnv.putAll(systemEnvService.getFullSystemEnv());
            // 添加发布环境变量
            releaseEnv.putAll(this.getReleaseEnv(build, release));
        }
        // 设置部署操作
        List<ApplicationActionLogDO> releaseActions = this.setReleaseActions(actions, releaseMachines, releaseEnv, hasEnvCommand);
        releaseActions.forEach(applicationActionLogDAO::insert);
        // 设置日志参数
        EventParamsHolder.addParams(release);
        return releaseId;
    }

    @Override
    public Long copyAppRelease(Long id) {
        // 查询
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(id);
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        List<ApplicationReleaseMachineDO> machines = applicationReleaseMachineService.getReleaseMachines(id);
        List<Long> machineIdList = machines.stream()
                .map(ApplicationReleaseMachineDO::getMachineId)
                .collect(Collectors.toList());
        // 提交
        ApplicationReleaseRequest request = new ApplicationReleaseRequest();
        request.setTitle(release.getReleaseTitle());
        request.setAppId(release.getAppId());
        request.setProfileId(release.getProfileId());
        request.setBuildId(release.getBuildId());
        request.setMachineIdList(machineIdList);
        return SpringHolder.getBean(ApplicationReleaseService.class).submitAppRelease(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer auditAppRelease(ApplicationReleaseAuditRequest request) {
        // 查询状态
        Long id = request.getId();
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(id);
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        ReleaseStatus status = ReleaseStatus.of(release.getReleaseStatus());
        if (!ReleaseStatus.WAIT_AUDIT.equals(status) && !ReleaseStatus.AUDIT_REJECT.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        AuditStatus auditStatus = AuditStatus.of(request.getStatus());
        UserDTO user = Currents.getUser();
        // 更新
        ApplicationReleaseDO update = new ApplicationReleaseDO();
        update.setId(id);
        update.setAuditUserId(user.getId());
        update.setAuditUserName(user.getUsername());
        update.setAuditTime(new Date());
        update.setAuditReason(request.getReason());
        final boolean resolve = AuditStatus.RESOLVE.equals(auditStatus);
        // 发送站内信
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.ID, release.getId());
        params.put(EventKeys.TITLE, release.getReleaseTitle());
        if (resolve) {
            // 通过
            final boolean timedRelease = TimedType.TIMED.getType().equals(release.getTimedRelease());
            if (!timedRelease) {
                update.setReleaseStatus(ReleaseStatus.WAIT_RUNNABLE.getStatus());
            } else {
                update.setReleaseStatus(ReleaseStatus.WAIT_SCHEDULE.getStatus());
                // 提交任务
                taskRegister.submit(TaskType.RELEASE, release.getTimedReleaseTime(), id);
            }
            webSideMessageService.addMessage(MessageType.RELEASE_AUDIT_RESOLVE, id, release.getCreateUserId(), release.getCreateUserName(), params);
        } else {
            // 驳回
            update.setReleaseStatus(ReleaseStatus.AUDIT_REJECT.getStatus());
            webSideMessageService.addMessage(MessageType.RELEASE_AUDIT_REJECT, id, release.getCreateUserId(), release.getCreateUserName(), params);
        }
        int effect = applicationReleaseDAO.updateById(update);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.TITLE, release.getReleaseTitle());
        EventParamsHolder.addParam(EventKeys.OPERATOR, resolve ? CnConst.RESOLVE : CnConst.REJECT);
        return effect;
    }

    @Override
    public void runnableAppRelease(Long id, boolean systemSchedule, boolean execute) {
        // 查询状态
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(id);
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        ReleaseStatus status = ReleaseStatus.of(release.getReleaseStatus());
        if (!ReleaseStatus.WAIT_RUNNABLE.equals(status)
                && !ReleaseStatus.WAIT_SCHEDULE.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 更新发布人
        ApplicationReleaseDO update = new ApplicationReleaseDO();
        update.setId(id);
        update.setUpdateTime(new Date());
        if (systemSchedule) {
            update.setReleaseUserId(release.getCreateUserId());
            update.setReleaseUserName(release.getCreateUserName());
        } else {
            UserDTO user = Currents.getUser();
            update.setReleaseUserId(user.getId());
            update.setReleaseUserName(user.getUsername());
            // 移除
            taskRegister.cancel(TaskType.RELEASE, id);
        }
        applicationReleaseDAO.updateById(update);
        // 发布
        if (execute) {
            IReleaseProcessor.with(release).exec();
        }
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.TITLE, release.getReleaseTitle());
        EventParamsHolder.addParam(EventKeys.SYSTEM, systemSchedule);
    }

    @Override
    public void cancelAppTimedRelease(Long id) {
        // 查询状态
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(id);
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        ReleaseStatus status = ReleaseStatus.of(release.getReleaseStatus());
        if (!ReleaseStatus.WAIT_SCHEDULE.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 更新状态
        ApplicationReleaseDO update = new ApplicationReleaseDO();
        update.setId(id);
        update.setUpdateTime(new Date());
        update.setTimedRelease(TimedType.NORMAL.getType());
        update.setReleaseStatus(ReleaseStatus.WAIT_RUNNABLE.getStatus());
        applicationReleaseDAO.updateById(update);
        applicationReleaseDAO.setTimedReleaseTimeNull(id);
        // 取消调度任务
        taskRegister.cancel(TaskType.RELEASE, id);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.TITLE, release.getReleaseTitle());
    }

    @Override
    public void setTimedRelease(Long id, Date releaseTime) {
        // 查询状态
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(id);
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        ReleaseStatus status = ReleaseStatus.of(release.getReleaseStatus());
        if (!ReleaseStatus.WAIT_RUNNABLE.equals(status) && !ReleaseStatus.WAIT_SCHEDULE.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 取消调度任务
        taskRegister.cancel(TaskType.RELEASE, id);
        // 更新状态
        ApplicationReleaseDO update = new ApplicationReleaseDO();
        update.setId(id);
        update.setUpdateTime(new Date());
        update.setReleaseStatus(ReleaseStatus.WAIT_SCHEDULE.getStatus());
        update.setTimedRelease(TimedType.TIMED.getType());
        update.setTimedReleaseTime(releaseTime);
        applicationReleaseDAO.updateById(update);
        // 提交任务
        taskRegister.submit(TaskType.RELEASE, releaseTime, id);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.TITLE, release.getReleaseTitle());
        EventParamsHolder.addParam(EventKeys.TIME, Dates.format(releaseTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long rollbackAppRelease(Long id) {
        // 查询状态
        ApplicationReleaseDO rollback = applicationReleaseDAO.selectById(id);
        Valid.notNull(rollback, MessageConst.RELEASE_ABSENT);
        ReleaseStatus status = ReleaseStatus.of(rollback.getReleaseStatus());
        if (!ReleaseStatus.FINISH.equals(status)) {
            throw Exceptions.argument(MessageConst.ILLEGAL_STATUS);
        }
        // 检查产物是否存在
        String path = Files1.getPath(SystemEnvAttr.DIST_PATH.getValue(), rollback.getBundlePath());
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
            List<ApplicationActionLogDO> actions = applicationActionLogService.selectActionByRelId(beforeId, StageType.RELEASE);
            for (ApplicationActionLogDO action : actions) {
                action.setId(null);
                action.setRelId(machine.getId());
                action.setLogPath(PathBuilders.getReleaseActionLogPath(releaseId, machine.getMachineId(), action.getActionId()));
                action.setRunStatus(ActionStatus.WAIT.getStatus());
                action.setExitCode(null);
                action.setStartTime(null);
                action.setEndTime(null);
                action.setCreateTime(null);
                action.setUpdateTime(null);
                applicationActionLogDAO.insert(action);
            }
        }
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.TITLE, rollback.getReleaseTitle());
        return releaseId;
    }

    @Override
    public void terminateRelease(Long id) {
        // 获取数据
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(id);
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        // 检查状态
        Valid.isTrue(ReleaseStatus.RUNNABLE.getStatus().equals(release.getReleaseStatus()), MessageConst.ILLEGAL_STATUS);
        // 获取实例
        IReleaseProcessor session = releaseSessionHolder.getSession(id);
        Valid.notNull(session, MessageConst.SESSION_PRESENT);
        // 调用终止
        session.terminateAll();
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.TITLE, release.getReleaseTitle());
    }


    @Override
    public void terminateMachine(Long releaseMachineId) {
        this.skipOrTerminateReleaseMachine(releaseMachineId, true);
    }

    @Override
    public void skipMachine(Long releaseMachineId) {
        this.skipOrTerminateReleaseMachine(releaseMachineId, false);
    }

    /**
     * 跳过或停止发布机器
     *
     * @param releaseMachineId releaseMachineId
     * @param terminate        terminate / skip
     */
    private void skipOrTerminateReleaseMachine(Long releaseMachineId, boolean terminate) {
        // 获取发布机器
        ApplicationReleaseMachineDO machine = applicationReleaseMachineDAO.selectById(releaseMachineId);
        Valid.notNull(machine, MessageConst.RELEASE_MACHINE_ABSENT);
        Long id = machine.getReleaseId();
        // 获取数据
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(id);
        Valid.notNull(release, MessageConst.RELEASE_ABSENT);
        // 检查状态
        Valid.isTrue(ReleaseStatus.RUNNABLE.getStatus().equals(release.getReleaseStatus()), MessageConst.ILLEGAL_STATUS);
        // 检查状态
        if (terminate) {
            Valid.isTrue(ActionStatus.RUNNABLE.getStatus().equals(machine.getRunStatus()), MessageConst.ILLEGAL_STATUS);
        } else {
            Valid.isTrue(ActionStatus.WAIT.getStatus().equals(machine.getRunStatus()), MessageConst.ILLEGAL_STATUS);
        }
        // 获取实例
        IReleaseProcessor session = releaseSessionHolder.getSession(id);
        Valid.notNull(session, MessageConst.SESSION_PRESENT);
        if (terminate) {
            // 调用终止
            session.terminateMachine(releaseMachineId);
        } else {
            // 调用跳过
            session.skipMachine(releaseMachineId);
        }
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.MACHINE_ID, releaseMachineId);
        EventParamsHolder.addParam(EventKeys.TITLE, release.getReleaseTitle());
        EventParamsHolder.addParam(EventKeys.MACHINE_NAME, machine.getMachineName());
    }

    @Override
    public void writeMachine(Long releaseMachineId, String command) {
        // 获取发布机器
        ApplicationReleaseMachineDO machine = applicationReleaseMachineDAO.selectById(releaseMachineId);
        Valid.notNull(machine, MessageConst.RELEASE_MACHINE_ABSENT);
        Valid.isTrue(ActionStatus.RUNNABLE.getStatus().equals(machine.getRunStatus()), MessageConst.ILLEGAL_STATUS);
        // 获取实例
        IReleaseProcessor session = releaseSessionHolder.getSession(machine.getReleaseId());
        Valid.notNull(session, MessageConst.SESSION_PRESENT);
        // 输入命令
        session.writeMachine(releaseMachineId, command);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteRelease(List<Long> idList) {
        // 查询状态
        List<ApplicationReleaseDO> releaseList = applicationReleaseDAO.selectBatchIds(idList);
        Valid.notEmpty(releaseList, MessageConst.RELEASE_ABSENT);
        boolean canDelete = releaseList.stream()
                .map(ApplicationReleaseDO::getReleaseStatus)
                .noneMatch(s -> ReleaseStatus.WAIT_SCHEDULE.getStatus().equals(s)
                        || ReleaseStatus.RUNNABLE.getStatus().equals(s));
        Valid.isTrue(canDelete, MessageConst.ILLEGAL_STATUS);
        // 查询机器
        List<Long> releaseMachineIdList = applicationReleaseMachineService.getReleaseMachineIdList(idList);
        // 删除主表
        int effect = applicationReleaseDAO.deleteBatchIds(idList);
        // 删除机器
        effect += applicationReleaseMachineDAO.deleteBatchIds(releaseMachineIdList);
        // 删除操作
        effect += applicationActionLogService.deleteByRelIdList(releaseMachineIdList, StageType.RELEASE);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, idList.size());
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
        if (machinesStatus.isEmpty()) {
            return machinesStatus;
        }
        // 查询操作状态
        List<ApplicationActionLogDO> actions = applicationActionLogDAO.selectStatusInfoByRelIdList(releaseMachineIdList, StageType.RELEASE.getType());
        for (ApplicationReleaseMachineStatusVO machineStatus : machinesStatus) {
            List<ApplicationActionLogDO> machineActions = actions.stream()
                    .filter(s -> s.getRelId().equals(machineStatus.getId()))
                    .collect(Collectors.toList());
            machineStatus.setActions(Converts.toList(machineActions, ApplicationActionStatusVO.class));
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
        List<ApplicationActionLogDO> actions = applicationActionLogDAO.selectStatusInfoByRelId(releaseMachineId, StageType.RELEASE.getType());
        status.setActions(Converts.toList(actions, ApplicationActionStatusVO.class));
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
        List<Long> machineIdList = request.getMachineIdList();
        List<MachineInfoDO> machines = machineInfoDAO.selectBatchIds(machineIdList);
        Valid.isTrue(machineIdList.size() == machines.size());
        return machines.stream().collect(Collectors.toMap(MachineInfoDO::getId, Function.identity()));
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
                                                ApplicationBuildDO build,
                                                List<ApplicationActionDO> actions) {
        UserDTO user = Currents.getUser();
        String buildBundlePath = applicationBuildService.checkBuildBundlePath(build);
        // 查询产物传输路径
        String transferPath = applicationEnvService.getAppEnvValue(app.getId(), profile.getId(), ApplicationEnvAttr.TRANSFER_PATH.getKey());
        // 查询产物传输方式
        String transferMode = applicationEnvService.getAppEnvValue(app.getId(), profile.getId(), ApplicationEnvAttr.TRANSFER_MODE.getKey());
        // 查询发布序列
        String releaseSerial = applicationEnvService.getAppEnvValue(app.getId(), profile.getId(), ApplicationEnvAttr.RELEASE_SERIAL.getKey());
        // 查询异常处理
        String exceptionHandler = applicationEnvService.getAppEnvValue(app.getId(), profile.getId(), ApplicationEnvAttr.EXCEPTION_HANDLER.getKey());
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
        release.setReleaseSerialize(SerialType.of(releaseSerial).getType());
        release.setExceptionHandler(ExceptionHandlerType.of(exceptionHandler).getType());
        release.setBundlePath(buildBundlePath);
        release.setTransferPath(transferPath);
        release.setTransferMode(transferMode);
        release.setTimedRelease(request.getTimedRelease());
        release.setTimedReleaseTime(request.getTimedReleaseTime());
        release.setCreateUserId(user.getId());
        release.setCreateUserName(user.getUsername());
        release.setActionConfig(JSON.toJSONString(actions));
        // 设置审核信息
        this.setCreateAuditInfo(release, user, Const.ENABLE.equals(profile.getReleaseAudit()));
        // 设置状态
        request.setStatus(release.getReleaseStatus());
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
        rollback.setReleaseTitle(rollback.getReleaseTitle());
        rollback.setReleaseType(ReleaseType.ROLLBACK.getType());
        rollback.setRollbackReleaseId(rollback.getId());
        rollback.setTimedRelease(TimedType.NORMAL.getType());
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
     * @param hasEnvCommand   hasEnvCommand
     * @return actions
     */
    private List<ApplicationActionLogDO> setReleaseActions(List<ApplicationActionDO> actions, List<ApplicationReleaseMachineDO> releaseMachines,
                                                           Map<String, String> releaseEnv, boolean hasEnvCommand) {
        List<ApplicationActionLogDO> releaseActions = Lists.newList();
        for (ApplicationReleaseMachineDO releaseMachine : releaseMachines) {
            for (ApplicationActionDO action : actions) {
                ActionType actionType = ActionType.of(action.getActionType());
                Long machineId = releaseMachine.getMachineId();
                Long actionId = action.getId();
                // 设置机器操作
                ApplicationActionLogDO releaseAction = new ApplicationActionLogDO();
                releaseAction.setRelId(releaseMachine.getId());
                releaseAction.setStageType(StageType.RELEASE.getType());
                releaseAction.setMachineId(releaseMachine.getMachineId());
                releaseAction.setActionId(actionId);
                releaseAction.setActionName(action.getActionName());
                releaseAction.setActionType(action.getActionType());
                // 设置命令
                String command = action.getActionCommand();
                if (ActionType.RELEASE_COMMAND.equals(actionType)) {
                    if (hasEnvCommand) {
                        // 替换发布命令
                        command = Strings.format(command, EnvConst.SYMBOL, releaseEnv);
                        // 替换机器命令
                        Map<String, String> machineEnv = machineEnvService.getFullMachineEnv(machineId);
                        command = Strings.format(command, EnvConst.SYMBOL, machineEnv);
                    }
                }
                releaseAction.setActionCommand(command);
                releaseAction.setLogPath(PathBuilders.getReleaseActionLogPath(releaseMachine.getReleaseId(), machineId, actionId));
                releaseAction.setRunStatus(ActionStatus.WAIT.getStatus());
                releaseActions.add(releaseAction);
            }
        }
        return releaseActions;
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
            if (TimedType.TIMED.getType().equals(release.getTimedRelease())) {
                release.setReleaseStatus(ReleaseStatus.WAIT_SCHEDULE.getStatus());
            } else {
                release.setReleaseStatus(ReleaseStatus.WAIT_RUNNABLE.getStatus());
            }
            release.setAuditUserId(user.getId());
            release.setAuditUserName(user.getUsername());
            release.setAuditTime(new Date());
            if (isAdmin) {
                release.setAuditReason(MessageConst.AUTO_AUDIT_RESOLVE);
            } else {
                release.setAuditReason(MessageConst.AUDIT_NOT_REQUIRED);
            }
        }
    }

    /**
     * 获取发布环境变量
     *
     * @param build   build
     * @param release release
     * @return env
     */
    private Map<String, String> getReleaseEnv(ApplicationBuildDO build, ApplicationReleaseDO release) {
        // 设置变量
        MutableLinkedHashMap<String, String> env = Maps.newMutableLinkedMap();
        env.put(EnvConst.RELEASE_PREFIX + EnvConst.BUILD_ID, build.getId() + Const.EMPTY);
        env.put(EnvConst.RELEASE_PREFIX + EnvConst.BUILD_SEQ, build.getBuildSeq() + Const.EMPTY);
        env.put(EnvConst.RELEASE_PREFIX + EnvConst.BRANCH, build.getBranchName() + Const.EMPTY);
        env.put(EnvConst.RELEASE_PREFIX + EnvConst.COMMIT, build.getCommitId() + Const.EMPTY);
        env.put(EnvConst.RELEASE_PREFIX + EnvConst.BUNDLE_PATH, release.getBundlePath() + Const.EMPTY);
        env.put(EnvConst.RELEASE_PREFIX + EnvConst.RELEASE_ID, release.getId() + Const.EMPTY);
        env.put(EnvConst.RELEASE_PREFIX + EnvConst.RELEASE_TITLE, release.getReleaseTitle());
        env.put(EnvConst.RELEASE_PREFIX + EnvConst.TRANSFER_PATH, release.getTransferPath());
        return env;
    }

}
