package com.orion.ops.service.impl;

import com.orion.ops.consts.*;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.consts.app.ApplicationEnvAttr;
import com.orion.ops.consts.app.ReleaseStatus;
import com.orion.ops.consts.app.ReleaseType;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.ApplicationReleaseAuditRequest;
import com.orion.ops.entity.request.ApplicationReleaseBillRequest;
import com.orion.ops.entity.request.ApplicationReleaseSubmitRequest;
import com.orion.ops.entity.vo.ApplicationDeployActionVO;
import com.orion.ops.handler.release.processor.IReleaseProcessor;
import com.orion.ops.handler.release.processor.ReleaseProcessorFactory;
import com.orion.ops.service.api.*;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Maps;
import com.orion.utils.io.Files1;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 上线单服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/10 20:33
 */
@Service("applicationReleaseService")
public class ApplicationReleaseServiceImpl implements ApplicationReleaseService {

    @Resource
    private ApplicationProfileDAO applicationProfileDAO;

    @Resource
    private ApplicationInfoService applicationInfoService;

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Resource
    private ApplicationEnvService applicationEnvService;

    @Resource
    private ApplicationMachineService applicationMachineService;

    @Resource
    private ApplicationDeployActionService applicationDeployActionService;

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private MachineEnvService machineEnvService;

    @Resource
    private ReleaseBillDAO releaseBillDAO;

    @Resource
    private ReleaseMachineDAO releaseMachineDAO;

    @Resource
    private ReleaseActionDAO releaseActionDAO;

    @Resource
    private ReleaseInfoService releaseInfoService;

    @Resource
    private ReleaseProcessorFactory releaseProcessorFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitAppRelease(ApplicationReleaseSubmitRequest request) {
        Long appId = request.getAppId();
        Long profileId = request.getProfileId();
        UserDTO user = Currents.getUser();
        // 查询应用环境
        boolean isConfig = applicationInfoService.checkAppConfig(appId, profileId);
        Valid.isTrue(isConfig, MessageConst.APP_PROFILE_NOT_CONFIGURED);
        ApplicationInfoDO app = applicationInfoDAO.selectById(appId);
        Valid.notNull(app, MessageConst.APP_ABSENT);
        ApplicationProfileDO profile = applicationProfileDAO.selectById(profileId);
        Valid.notNull(profile, MessageConst.PROFILE_ABSENT);
        // 插入上线单信息
        ReleaseBillDO releaseBill = this.insertReleaseBill(request, user, app, profile);
        // 插入机器信息
        List<ReleaseMachineDO> machines = this.insertReleaseMachine(releaseBill, request);
        // 应用环境变量
        Map<String, String> appEnv = this.getAppReleaseEnv(app, profile);
        // 插入部署操作
        this.insertReleaseAction(releaseBill, machines, appEnv);
        return releaseBill.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copyAppRelease(Long id) {
        // 查询上线单
        ReleaseBillDO sourceRelease = releaseBillDAO.selectById(id);
        Valid.notNull(sourceRelease, MessageConst.RELEASE_BILL_ABSENT);
        if (sourceRelease.getRollbackReleaseId() != null) {
            throw Exceptions.argument(MessageConst.RELEASE_TYPE_UNABLE_COPY);
        }
        // 复制
        return this.copyReleaseBill(sourceRelease, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long rollbackAppRelease(Long id) {
        // 查询上线单
        ReleaseBillDO rollbackRelease = releaseBillDAO.selectById(id);
        Valid.notNull(rollbackRelease, MessageConst.RELEASE_BILL_ABSENT);
        if (rollbackRelease.getRollbackReleaseId() != null) {
            throw Exceptions.argument(MessageConst.RELEASE_TYPE_UNABLE_COPY);
        }
        // 检查状态
        ReleaseStatus status = ReleaseStatus.of(rollbackRelease.getReleaseStatus());
        if (ReleaseStatus.WAIT_AUDIT.equals(status)
                || ReleaseStatus.AUDIT_REJECT.equals(status)
                || ReleaseStatus.WAIT_RUNNABLE.equals(status)
                || ReleaseStatus.RUNNABLE.equals(status)
                || ReleaseStatus.EXCEPTION.equals(status)) {
            throw Exceptions.argument(MessageConst.STATUS_UNABLE_ROLLBACK_RELEASE);
        }
        // 检查快照产物
        String distSnapshotPath = MachineEnvAttr.DIST_PATH.getValue() + rollbackRelease.getDistSnapshotPath();
        Valid.isTrue(Files1.isFile(distSnapshotPath), MessageConst.FILE_ABSENT_UNABLE_ROLLBACK_RELEASE);
        // 复制
        return this.copyReleaseBill(rollbackRelease, true);
    }

    @Override
    public Integer auditAppRelease(ApplicationReleaseAuditRequest request) {
        UserDTO user = Currents.getUser();
        // 查询上线单
        ReleaseBillDO releaseBill = releaseBillDAO.selectById(request.getId());
        Valid.notNull(releaseBill, MessageConst.RELEASE_BILL_ABSENT);
        if (!ReleaseStatus.WAIT_AUDIT.getStatus().equals(releaseBill.getReleaseStatus())) {
            return 0;
        }
        AuditStatus status = AuditStatus.of(request.getStatus());
        // 更新
        ReleaseBillDO update = new ReleaseBillDO();
        update.setId(releaseBill.getId());
        update.setAuditUserId(user.getId());
        update.setAuditUserName(user.getUsername());
        update.setAuditTime(new Date());
        update.setAuditReason(request.getReason());
        if (AuditStatus.RESOLVE.equals(status)) {
            // 通过
            update.setReleaseStatus(ReleaseStatus.WAIT_RUNNABLE.getStatus());
        } else if (AuditStatus.REJECT.equals(status)) {
            // 驳回
            update.setReleaseStatus(ReleaseStatus.AUDIT_REJECT.getStatus());
        } else {
            return 0;
        }
        return releaseBillDAO.updateById(update);
    }

    @Override
    public void runnableAppRelease(ApplicationReleaseBillRequest request) {
        Long id = request.getId();
        ReleaseBillDO releaseBill = releaseInfoService.getReleaseBill(id);
        Valid.notNull(releaseBill, MessageConst.RELEASE_BILL_ABSENT);
        // 检查状态
        ReleaseStatus status = ReleaseStatus.of(releaseBill.getReleaseStatus());
        if (ReleaseStatus.WAIT_AUDIT.equals(status)
                || ReleaseStatus.AUDIT_REJECT.equals(status)
                || ReleaseStatus.RUNNABLE.equals(status)
                || ReleaseStatus.FINISH.equals(status)) {
            throw Exceptions.argument(MessageConst.STATUS_UNABLE_RUNNABLE_RELEASE);
        }
        // 修改
        UserDTO user = Currents.getUser();
        ReleaseBillDO update = new ReleaseBillDO();
        update.setId(id);
        update.setReleaseUserId(user.getId());
        update.setReleaseUserName(user.getUsername());
        releaseBillDAO.updateById(update);
        // 创建且运行
        IReleaseProcessor processor = releaseProcessorFactory.createReleaseProcessor(id);
        SchedulerPools.RELEASE_MAIN_SCHEDULER.execute(processor);
    }

    /**
     * 插入上线单
     *
     * @param request request
     * @param user    user
     * @param app     app
     * @param profile profile
     * @return bill
     */
    private ReleaseBillDO insertReleaseBill(ApplicationReleaseSubmitRequest request, UserDTO user, ApplicationInfoDO app, ApplicationProfileDO profile) {
        // 构建数据
        ReleaseBillDO releaseBill = new ReleaseBillDO();
        releaseBill.setReleaseTitle(request.getTitle());
        releaseBill.setReleaseDescription(request.getDescription());
        releaseBill.setAppId(app.getId());
        releaseBill.setAppName(app.getAppName());
        releaseBill.setAppTag(app.getAppTag());
        releaseBill.setProfileId(profile.getId());
        releaseBill.setProfileName(profile.getProfileName());
        releaseBill.setProfileTag(profile.getProfileTag());
        releaseBill.setReleaseType(ReleaseType.NORMAL.getType());
        // 设置审核状态
        this.setCreateAuditInfo(releaseBill, user, Const.ENABLE.equals(profile.getReleaseAudit()));
        // 查询环境变量
        String distPath = applicationEnvService.getAppEnvValue(app.getId(), profile.getId(), ApplicationEnvAttr.DIST_PATH.getKey());
        String vcsLocalPath = applicationEnvService.getAppEnvValue(app.getId(), profile.getId(), ApplicationEnvAttr.VCS_ROOT_PATH.getKey());
        releaseBill.setVcsLocalPath(vcsLocalPath);
        releaseBill.setVcsRemoteUrl(request.getVcsUrl());
        releaseBill.setBranchName(request.getBranchName());
        releaseBill.setCommitId(request.getCommitId());
        releaseBill.setDistPath(distPath);
        releaseBill.setCreateUserId(user.getId());
        releaseBill.setCreateUserName(user.getUsername());
        // 插入
        releaseBillDAO.insert(releaseBill);
        Long id = releaseBill.getId();
        // 修改
        ReleaseBillDO update = new ReleaseBillDO();
        update.setId(id);
        // 产物快照路径
        update.setDistSnapshotPath(PathBuilders.getDistSnapshotPath(id, distPath));
        // 日志路径
        update.setLogPath(PathBuilders.getReleaseHostLogPath(id));
        releaseBillDAO.updateById(update);
        return releaseBill;
    }

    /**
     * 插入上线单机器
     *
     * @param releaseBill releaseBill
     * @param request     request
     * @return machines
     */
    private List<ReleaseMachineDO> insertReleaseMachine(ReleaseBillDO releaseBill, ApplicationReleaseSubmitRequest request) {
        List<ReleaseMachineDO> list = Lists.newList();
        for (Long id : request.getMachineIdList()) {
            // 查询机器信息
            Long machineId = applicationMachineService.getAppProfileMachineId(id, request.getAppId(), request.getProfileId());
            Valid.notNull(machineId, MessageConst.INVALID_MACHINE);
            MachineInfoDO machine = machineInfoDAO.selectById(machineId);
            Valid.notNull(machine, MessageConst.INVALID_MACHINE);
            // 构建数据
            ReleaseMachineDO releaseMachine = new ReleaseMachineDO();
            releaseMachine.setReleaseId(releaseBill.getId());
            releaseMachine.setMachineId(machineId);
            releaseMachine.setMachineName(machine.getMachineName());
            releaseMachine.setMachineTag(machine.getMachineTag());
            releaseMachine.setMachineHost(machine.getMachineHost());
            releaseMachine.setLogPath(PathBuilders.getReleaseTargetMachineLogPath(releaseBill.getId(), machineId));
            // 获取机器分发路径
            String distPathEnv = machineEnvService.getMachineEnv(machineId, MachineEnvAttr.DIST_PATH.getKey());
            String distPath = Files1.getPath(distPathEnv + "/" + Files1.getFileName(releaseBill.getDistPath()));
            releaseMachine.setDistPath(distPath);
            list.add(releaseMachine);
        }
        list.forEach(releaseMachineDAO::insert);
        return list;
    }

    /**
     * 插入上线单操作
     *
     * @param releaseBill releaseBill
     * @param machines    机器信息
     */
    private void insertReleaseAction(ReleaseBillDO releaseBill, List<ReleaseMachineDO> machines, Map<String, String> appEnv) {
        List<ReleaseActionDO> list = Lists.newList();
        // 查询步骤
        List<ApplicationDeployActionVO> actions = applicationDeployActionService.getDeployActions(releaseBill.getAppId(), releaseBill.getProfileId());
        List<ApplicationDeployActionVO> hostActions = actions.stream()
                .filter(a -> ActionType.isHost(a.getType()))
                .collect(Collectors.toList());
        // 插入宿主机默认步骤
        ApplicationDeployActionVO connectAction = new ApplicationDeployActionVO();
        connectAction.setName(Const.CONNECT);
        connectAction.setType(ActionType.CONNECT.getType());
        hostActions.add(0, connectAction);
        // 查询宿主机环境变量
        Map<String, String> hostMachineEnv = this.getMachineReleaseEnv(Const.HOST_MACHINE_ID);
        hostMachineEnv.put(EnvConst.DIST_PATH, releaseBill.getDistPath());

        // 设置宿主机操作步骤
        for (ApplicationDeployActionVO action : hostActions) {
            ReleaseActionDO targetAction = this.toActionDoWithActionVO(action, releaseBill.getId(), Const.HOST_MACHINE_ID, hostMachineEnv, appEnv);
            list.add(targetAction);
        }
        // 设置机器操作步骤
        for (ReleaseMachineDO machine : machines) {
            Long machineId = machine.getMachineId();
            // 查询机器环境变量
            Map<String, String> machineEnv = this.getMachineReleaseEnv(machineId);
            machineEnv.put(EnvConst.TARGET_DIST_PATH, machine.getDistPath());
            // 设置目标机器步骤
            for (ApplicationDeployActionVO action : actions) {
                if (ActionType.isHost(action.getType())) {
                    continue;
                }
                ReleaseActionDO targetAction = this.toActionDoWithActionVO(action, releaseBill.getId(), machineId, machineEnv, appEnv);
                list.add(targetAction);
            }
        }
        // 插入
        list.forEach(releaseActionDAO::insert);
        // 设置日志路径
        for (ReleaseActionDO action : list) {
            ReleaseActionDO update = new ReleaseActionDO();
            update.setId(action.getId());
            update.setLogPath(PathBuilders.getReleaseActionLogPath(action.getReleaseId(), action.getId()));
            releaseActionDAO.updateById(update);
        }
    }

    /**
     * 复制上线单
     *
     * @param sourceRelease sourceRelease
     * @param isRollback    true 回滚 false 复制
     * @return copyId
     */
    private Long copyReleaseBill(ReleaseBillDO sourceRelease, boolean isRollback) {
        Long sourceReleaseId = sourceRelease.getId();
        UserDTO user = Currents.getUser();
        // 检查应用环境
        ApplicationInfoDO app = applicationInfoDAO.selectById(sourceRelease.getAppId());
        Valid.notNull(app, MessageConst.APP_ABSENT);
        ApplicationProfileDO profile = applicationProfileDAO.selectById(sourceRelease.getProfileId());
        Valid.notNull(profile, MessageConst.PROFILE_ABSENT);
        // 构建数据
        ReleaseBillDO copyRelease = new ReleaseBillDO();
        if (isRollback) {
            copyRelease.setReleaseTitle(sourceRelease.getReleaseTitle() + " - " + Const.ROLLBACK);
            copyRelease.setReleaseType(ReleaseType.ROLLBACK.getType());
            copyRelease.setRollbackReleaseId(sourceReleaseId);
            copyRelease.setDistPath(MachineEnvAttr.DIST_PATH.getValue() + sourceRelease.getDistSnapshotPath());
        } else {
            copyRelease.setReleaseTitle(sourceRelease.getReleaseTitle() + " - " + Const.COPY);
            copyRelease.setReleaseType(ReleaseType.NORMAL.getType());
            copyRelease.setDistPath(sourceRelease.getDistPath());
        }
        copyRelease.setReleaseDescription(sourceRelease.getReleaseDescription());
        copyRelease.setAppId(sourceRelease.getAppId());
        copyRelease.setAppName(sourceRelease.getAppName());
        copyRelease.setAppTag(sourceRelease.getAppTag());
        copyRelease.setProfileId(sourceRelease.getProfileId());
        copyRelease.setProfileName(sourceRelease.getProfileName());
        copyRelease.setProfileTag(sourceRelease.getProfileTag());
        copyRelease.setVcsLocalPath(sourceRelease.getVcsLocalPath());
        copyRelease.setVcsRemoteUrl(sourceRelease.getVcsRemoteUrl());
        copyRelease.setBranchName(sourceRelease.getBranchName());
        copyRelease.setCommitId(sourceRelease.getCommitId());
        copyRelease.setCreateUserId(user.getId());
        copyRelease.setCreateUserName(user.getUsername());
        this.setCreateAuditInfo(copyRelease, user, Const.ENABLE.equals(profile.getReleaseAudit()));
        releaseBillDAO.insert(copyRelease);
        Long copyId = copyRelease.getId();
        // 更新
        ReleaseBillDO update = new ReleaseBillDO();
        update.setId(copyId);
        update.setLogPath(PathBuilders.getReleaseHostLogPath(copyId));
        update.setDistSnapshotPath(PathBuilders.getDistSnapshotPath(copyId, sourceRelease.getDistPath()));
        releaseBillDAO.updateById(update);
        // 复制机器
        releaseInfoService.copyReleaseMachine(sourceReleaseId, copyId);
        // 复制操作
        releaseInfoService.copyReleaseAction(sourceReleaseId, copyId);
        return copyId;
    }

    /**
     * 获取应用环境变量
     *
     * @param app     app
     * @param profile profile
     * @return env
     */
    private Map<String, String> getAppReleaseEnv(ApplicationInfoDO app, ApplicationProfileDO profile) {
        // 插入环境信息
        Map<String, String> envs = Maps.newLinkedMap();
        envs.put(EnvConst.APP_NAME, app.getAppName());
        envs.put(EnvConst.APP_TAG, app.getAppTag());
        envs.put(EnvConst.PROFILE_NAME, profile.getProfileName());
        envs.put(EnvConst.PROFILE_TAG, profile.getProfileTag());
        // 插入应用环境变量
        Map<String, String> appProfileEnvs = applicationEnvService.getAppProfileEnv(app.getId(), profile.getId());
        appProfileEnvs.forEach((k, v) -> {
            envs.put(EnvConst.APP_PREFIX + k, v);
        });
        return envs;
    }

    /**
     * 获取机器环境变量
     *
     * @param machineId machineId
     * @return env
     */
    private Map<String, String> getMachineReleaseEnv(Long machineId) {
        return machineEnvService.getFullMachineEnv(machineId, EnvConst.MACHINE_PREFIX);
    }

    /**
     * 创建时设置是否需要审核
     *
     * @param releaseBill releaseBill
     * @param user        user
     * @param needAudit   needAudit
     */
    private void setCreateAuditInfo(ReleaseBillDO releaseBill, UserDTO user, boolean needAudit) {
        boolean isAdmin = RoleType.isAdministrator(user.getRoleType());
        // 需要审核 & 不是管理员
        if (needAudit && !isAdmin) {
            releaseBill.setReleaseStatus(ReleaseStatus.WAIT_AUDIT.getStatus());
        } else {
            releaseBill.setReleaseStatus(ReleaseStatus.WAIT_RUNNABLE.getStatus());
            releaseBill.setAuditUserId(user.getId());
            releaseBill.setAuditUserName(user.getUsername());
            releaseBill.setAuditTime(new Date());
            if (isAdmin) {
                releaseBill.setAuditReason(MessageConst.AUTO_AUDIT_RESOLVE);
            } else {
                releaseBill.setAuditReason(MessageConst.AUDIT_NO_REQUIRED);
            }
        }
    }

    /**
     * ApplicationDeployActionVO > ReleaseActionDO
     *
     * @param action     ApplicationDeployActionVO
     * @param releaseId  releaseId
     * @param machineId  machineId
     * @param machineEnv machineEnv
     * @param appEnv     appEnv
     * @return ReleaseActionDO
     */
    private ReleaseActionDO toActionDoWithActionVO(ApplicationDeployActionVO action, Long releaseId, Long machineId, Map<String, String> machineEnv, Map<String, String> appEnv) {
        ReleaseActionDO realAction = new ReleaseActionDO();
        realAction.setReleaseId(releaseId);
        realAction.setMachineId(machineId);
        realAction.setActionId(action.getId());
        realAction.setActionName(action.getName());
        realAction.setActionType(action.getType());
        String command = action.getCommand();
        if (!Strings.isBlank(command)) {
            // 替换应用环境变量占位符
            command = Strings.format(command, EnvConst.SYMBOL, appEnv);
            // 替换机器环境变量占位符
            command = Strings.format(command, EnvConst.SYMBOL, machineEnv);
            realAction.setActionCommand(command);
        }
        return realAction;
    }

}
