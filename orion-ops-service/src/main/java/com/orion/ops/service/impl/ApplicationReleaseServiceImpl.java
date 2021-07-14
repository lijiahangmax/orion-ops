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
    private ReleaseAppEnvDAO releaseAppEnvDAO;

    @Resource
    private ReleaseMachineEnvDAO releaseMachineEnvDAO;

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
        this.insertNormalAppReleaseEnv(releaseId, app, profile);
        // 插入机器环境变量
        this.insertNormalMachineReleaseEnv(releaseId, machines);
        // 插入部署操作
        this.insertNormalReleaseAction(releaseId, request);
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
        for (Long id : request.getAppMachineIdList()) {
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
     * 插入应用环境变量
     *
     * @param releaseId releaseId
     * @param app       app
     * @param profile   profile
     */
    private void insertNormalAppReleaseEnv(Long releaseId, ApplicationInfoDO app, ApplicationProfileDO profile) {
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
        // 插入
        envs.forEach((k, v) -> {
            ReleaseAppEnvDO env = new ReleaseAppEnvDO();
            env.setReleaseId(releaseId);
            env.setEnvKey(k);
            env.setEnvValue(v);
            releaseAppEnvDAO.insert(env);
        });
    }

    /**
     * 插入应用环境变量
     *
     * @param releaseId releaseId
     * @param machines  machines
     */
    private void insertNormalMachineReleaseEnv(Long releaseId, List<ReleaseMachineDO> machines) {
        for (ReleaseMachineDO releaseMachine : machines) {
            Map<String, String> envs = Maps.newLinkedMap();
            // 查询机器
            Long machineId = releaseMachine.getMachineId();
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
            // 插入
            envs.forEach((k, v) -> {
                ReleaseMachineEnvDO env = new ReleaseMachineEnvDO();
                env.setReleaseId(releaseId);
                env.setMachineId(machineId);
                env.setEnvKey(k);
                env.setEnvValue(v);
                releaseMachineEnvDAO.insert(env);
            });
        }
    }

    /**
     * 插入部署步骤信息
     *
     * @param releaseId releaseId
     * @param request   request
     */
    private void insertNormalReleaseAction(Long releaseId, ApplicationReleaseSubmitRequest request) {
        List<ReleaseActionDO> list = Lists.newList();
        // 插入默认步骤
        ReleaseActionDO init = new ReleaseActionDO();
        init.setReleaseId(releaseId);
        init.setActionName(Const.INIT);
        init.setActionType(ActionType.INIT.getType());
        list.add(init);
        ReleaseActionDO connect = new ReleaseActionDO();
        connect.setReleaseId(releaseId);
        connect.setActionName(Const.CONNECT);
        connect.setActionType(ActionType.CONNECT.getType());
        list.add(connect);
        // 查询步骤
        List<ApplicationDeployActionVO> actions = applicationDeployActionService.getDeployActions(request.getAppId(), request.getProfileId());
        // 插入
        actions.stream().map(a -> {
            ReleaseActionDO action = new ReleaseActionDO();
            action.setReleaseId(releaseId);
            action.setActionId(a.getId());
            action.setActionName(a.getName());
            action.setActionType(a.getType());
            action.setActionCommand(a.getCommand());
            return action;
        }).forEach(releaseActionDAO::insert);
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
