package com.orion.ops.service.impl;

import com.orion.ops.consts.AuditStatus;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.RoleType;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.consts.app.ApplicationEnvAttr;
import com.orion.ops.consts.app.ReleaseStatus;
import com.orion.ops.consts.app.ReleaseType;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.ApplicationReleaseAuditRequest;
import com.orion.ops.entity.request.ApplicationReleaseBillRequest;
import com.orion.ops.entity.request.ApplicationReleaseSubmitRequest;
import com.orion.ops.entity.vo.ApplicationDeployActionVO;
import com.orion.ops.service.api.*;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Maps;
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
        ReleaseBillDO releaseBill = this.insertNormalReleaseBill(request, user, app, profile);
        Long releaseId = releaseBill.getId();
        // 插入机器信息
        List<ReleaseMachineDO> machines = this.insertNormalReleaseMachine(releaseId, request);
        // 插入应用环境变量
        Map<String, String> appEnv = this.getAppReleaseEnv(app, profile);
        // 插入部署操作
        this.insertNormalReleaseAction(releaseId, request, machines, appEnv);
        return releaseId;
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
        envs.put("appName", app.getAppName());
        envs.put("appTag", app.getAppTag());
        envs.put("profileName", profile.getProfileName());
        envs.put("profileTag", profile.getProfileTag());
        // 插入应用环境变量
        Map<String, String> appProfileEnvs = applicationEnvService.getAppProfileEnv(app.getId(), profile.getId());
        appProfileEnvs.forEach((k, v) -> {
            envs.put("app." + k, v);
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
        Map<String, String> envs = Maps.newLinkedMap();
        // 查询机器
        MachineInfoDO machine = machineInfoDAO.selectById(machineId);
        envs.put("machineName", machine.getMachineName());
        envs.put("machineTag", machine.getMachineTag());
        envs.put("machineHost", machine.getMachineHost());
        envs.put("machinePort", machine.getSshPort() + Strings.EMPTY);
        envs.put("machineUsername", machine.getUsername());
        // 查询环境变量
        Map<String, String> machineEnvs = machineEnvService.getMachineEnv(machineId);
        machineEnvs.forEach((k, v) -> {
            envs.put("machine." + k, v);
        });
        return envs;
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
    private ReleaseBillDO insertNormalReleaseBill(ApplicationReleaseSubmitRequest request, UserDTO user, ApplicationInfoDO app, ApplicationProfileDO profile) {
        boolean isAdmin = RoleType.isAdministrator(user.getRoleType());
        boolean needAudit = Const.ENABLE.equals(profile.getReleaseAudit());
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
        this.setCreateAuditInfo(releaseBill, user, needAudit, isAdmin);
        String vcsLocalPath = applicationEnvService.getAppEnvValue(app.getId(), profile.getId(), ApplicationEnvAttr.VCS_ROOT_PATH.name());
        releaseBill.setVcsLocalPath(vcsLocalPath);
        releaseBill.setVcsRemoteUrl(request.getVcsUrl());
        releaseBill.setBranchName(request.getBranchName());
        releaseBill.setCommitId(request.getCommitId());
        releaseBill.setCommitMessage(request.getCommitMessage());
        // 查询环境变量
        String distPath = applicationEnvService.getAppEnvValue(app.getId(), profile.getId(), ApplicationEnvAttr.DIST_PATH.name());
        releaseBill.setDistPath(distPath);
        releaseBill.setCreateUserId(user.getId());
        releaseBill.setCreateUserName(user.getUsername());
        releaseBillDAO.insert(releaseBill);
        return releaseBill;
    }

    /**
     * 插入上线单机器
     *
     * @param releaseId releaseId
     * @param request   request
     * @return machines
     */
    private List<ReleaseMachineDO> insertNormalReleaseMachine(Long releaseId, ApplicationReleaseSubmitRequest request) {
        List<ReleaseMachineDO> list = Lists.newList();
        for (Long id : request.getMachineIdList()) {
            // 查询机器信息
            Long machineId = applicationMachineService.getAppProfileMachineId(id, request.getAppId(), request.getProfileId());
            Valid.notNull(machineId, MessageConst.INVALID_MACHINE);
            MachineInfoDO machine = machineInfoDAO.selectById(machineId);
            Valid.notNull(machine, MessageConst.INVALID_MACHINE);
            // 构建数据
            ReleaseMachineDO releaseMachine = new ReleaseMachineDO();
            releaseMachine.setReleaseId(releaseId);
            releaseMachine.setMachineId(machineId);
            releaseMachine.setMachineName(machine.getMachineName());
            releaseMachine.setMachineTag(machine.getMachineTag());
            releaseMachine.setMachineHost(machine.getMachineHost());
            list.add(releaseMachine);
        }
        list.forEach(releaseMachineDAO::insert);
        return list;
    }

    /**
     * 插入部署步骤信息
     *
     * @param releaseId releaseId
     * @param request   request
     */
    private void insertNormalReleaseAction(Long releaseId, ApplicationReleaseSubmitRequest request, List<ReleaseMachineDO> machines, Map<String, String> appEnv) {
        List<ReleaseActionDO> list = Lists.newList();
        // 查询步骤
        List<ApplicationDeployActionVO> actions = applicationDeployActionService.getDeployActions(request.getAppId(), request.getProfileId());
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
        // 设置宿主机操作步骤
        for (ApplicationDeployActionVO action : hostActions) {
            ReleaseActionDO targetAction = toActionDoWithActionVO(action, releaseId, Const.HOST_MACHINE_ID, hostMachineEnv, appEnv);
            list.add(targetAction);
        }
        // 设置机器操作步骤
        for (ReleaseMachineDO machine : machines) {
            Long machineId = machine.getMachineId();
            // 查询机器环境变量
            Map<String, String> machineEnv = this.getMachineReleaseEnv(machineId);
            // 设置目标机器步骤
            for (ApplicationDeployActionVO action : actions) {
                if (ActionType.isHost(action.getType())) {
                    continue;
                }
                ReleaseActionDO targetAction = toActionDoWithActionVO(action, releaseId, machineId, machineEnv, appEnv);
                list.add(targetAction);
            }
        }
        // 插入
        list.forEach(releaseActionDAO::insert);
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
            command = Strings.format(command, "#", appEnv);
            // 替换机器环境变量占位符
            command = Strings.format(command, "#", machineEnv);
            realAction.setActionCommand(command);
        }
        return realAction;
    }

    /**
     * 创建时设置是否需要审核
     *
     * @param releaseBill releaseBill
     * @param user        user
     * @param needAudit   needAudit
     * @param isAdmin     isAdmin
     */
    private void setCreateAuditInfo(ReleaseBillDO releaseBill, UserDTO user, boolean needAudit, boolean isAdmin) {
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

}
