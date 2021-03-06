package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.ApplicationEnvAttr;
import com.orion.ops.dao.ApplicationDeployActionDAO;
import com.orion.ops.dao.ApplicationInfoDAO;
import com.orion.ops.dao.ApplicationMachineDAO;
import com.orion.ops.dao.ApplicationProfileDAO;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.request.*;
import com.orion.ops.entity.vo.*;
import com.orion.ops.service.api.*;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.collect.Lists;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import com.orion.vcs.git.Gits;
import com.orion.vcs.git.info.BranchInfo;
import com.orion.vcs.git.info.LogInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用服务实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 17:52
 */
@Service("applicationInfoService")
public class ApplicationInfoServiceImpl implements ApplicationInfoService {

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Resource
    private ApplicationMachineDAO applicationMachineDAO;

    @Resource
    private ApplicationMachineService applicationMachineService;

    @Resource
    private ApplicationProfileDAO applicationProfileDAO;

    @Resource
    private ApplicationEnvService applicationEnvService;

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private ApplicationDeployActionDAO applicationDeployActionDAO;

    @Resource
    private ApplicationDeployActionService applicationDeployActionService;

    @Resource
    private ReleaseInfoService releaseInfoService;

    @Override
    public Long insertApp(ApplicationInfoRequest request) {
        // 检查是否存在
        String name = request.getName();
        // 重复检查
        this.checkNamePresent(null, name);
        // 插入
        ApplicationInfoDO insert = new ApplicationInfoDO();
        insert.setAppName(name);
        insert.setAppTag(request.getTag());
        insert.setAppSort(request.getSort());
        insert.setDescription(request.getDescription());
        applicationInfoDAO.insert(insert);
        return insert.getId();
    }

    @Override
    public Integer updateApp(ApplicationInfoRequest request) {
        Long id = request.getId();
        String name = request.getName();
        // 重复检查
        this.checkNamePresent(id, name);
        // 更新
        ApplicationInfoDO update = new ApplicationInfoDO();
        update.setId(id);
        update.setAppName(name);
        update.setAppTag(request.getTag());
        update.setAppSort(request.getSort());
        update.setDescription(request.getDescription());
        update.setUpdateTime(new Date());
        return applicationInfoDAO.updateById(update);
    }

    @Override
    public Integer updateAppSort(Long id, boolean incr) {
        // 查询原来的排序
        ApplicationInfoDO app = applicationInfoDAO.selectById(id);
        Valid.notNull(app, MessageConst.APP_ABSENT);
        Integer beforeSort = app.getAppSort();
        // 查询下一个排序
        LambdaQueryWrapper<ApplicationInfoDO> wrapper;
        if (incr) {
            wrapper = new LambdaQueryWrapper<ApplicationInfoDO>()
                    .ne(ApplicationInfoDO::getId, id)
                    .le(ApplicationInfoDO::getAppSort, beforeSort)
                    .orderByDesc(ApplicationInfoDO::getAppSort)
                    .last(Const.LIMIT_1);
        } else {
            wrapper = new LambdaQueryWrapper<ApplicationInfoDO>()
                    .ne(ApplicationInfoDO::getId, id)
                    .ge(ApplicationInfoDO::getAppSort, beforeSort)
                    .orderByAsc(ApplicationInfoDO::getAppSort)
                    .last(Const.LIMIT_1);
        }
        // 查询需要交换的
        ApplicationInfoDO swapApp = applicationInfoDAO.selectOne(wrapper);
        if (swapApp == null) {
            return 0;
        }
        // 交换
        ApplicationInfoDO updateSwap = new ApplicationInfoDO();
        updateSwap.setId(swapApp.getId());
        updateSwap.setAppSort(beforeSort);
        applicationInfoDAO.updateById(updateSwap);
        // 更新
        Integer afterSort = swapApp.getAppSort();
        if (afterSort.equals(beforeSort)) {
            if (incr) {
                afterSort -= 1;
            } else {
                afterSort += 1;
            }
        }
        ApplicationInfoDO updateTarget = new ApplicationInfoDO();
        updateTarget.setId(id);
        updateTarget.setAppSort(afterSort);
        return applicationInfoDAO.updateById(updateTarget);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteApp(Long id) {
        int effect = 0;
        // 删除应用
        effect += applicationInfoDAO.deleteById(id);
        // 删除环境变量
        effect += applicationEnvService.deleteAppProfileEnvByAppProfileId(id, null);
        // 删除机器
        effect += applicationMachineService.deleteAppMachineByAppProfileId(id, null);
        // 删除部署步骤
        effect += applicationDeployActionService.deleteAppActionByAppProfileId(id, null);
        return effect;
    }

    @Override
    public DataGrid<ApplicationInfoVO> listApp(ApplicationInfoRequest request) {
        LambdaQueryWrapper<ApplicationInfoDO> wrapper = new LambdaQueryWrapper<ApplicationInfoDO>()
                .like(!Strings.isBlank(request.getName()), ApplicationInfoDO::getAppName, request.getName())
                .like(!Strings.isBlank(request.getTag()), ApplicationInfoDO::getAppTag, request.getTag())
                .orderByAsc(ApplicationInfoDO::getAppSort)
                .orderByAsc(ApplicationInfoDO::getId);
        // 查询应用
        DataGrid<ApplicationInfoVO> appList = DataQuery.of(applicationInfoDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(ApplicationInfoVO.class);
        if (appList.isEmpty() || request.getProfileId() == null) {
            return appList;
        }
        // 查询机器
        for (ApplicationInfoVO app : appList) {
            Integer machineCount = applicationMachineService.selectAppProfileMachineCount(app.getId(), request.getProfileId());
            app.setMachineCount(machineCount);
            if (machineCount == 0) {
                app.setIsConfig(Const.NOT_CONFIGURED);
            } else {
                app.setIsConfig(Const.CONFIGURED);
            }
        }
        return appList;
    }

    @Override
    public ApplicationDetailVO getAppDetail(Long appId, Long profileId) {
        // 查询应用
        ApplicationInfoDO app = applicationInfoDAO.selectById(appId);
        Valid.notNull(app, MessageConst.APP_ABSENT);
        // 查询环境
        ApplicationProfileDO profile = applicationProfileDAO.selectById(profileId);
        Valid.notNull(profile, MessageConst.PROFILE_ABSENT);
        // 查询环境变量
        String vcsRootPath = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.VCS_ROOT_PATH.getKey());
        String vcsCodePath = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.VCS_CODE_PATH.getKey());
        String vcsType = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.VCS_TYPE.getKey());
        String distPath = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.DIST_PATH.getKey());
        // 查询机器
        List<ApplicationMachineVO> machines = applicationMachineService.getAppProfileMachineList(appId, profileId);
        // 查询部署流程
        List<ApplicationDeployActionVO> actions = applicationDeployActionService.getDeployActions(appId, profileId);
        // 组装数据
        ApplicationDetailVO detail = Converts.to(app, ApplicationDetailVO.class);
        detail.setProfileId(profile.getId());
        detail.setProfileName(profile.getProfileName());
        detail.setProfileTag(profile.getProfileTag());
        detail.setVcsRootPath(vcsRootPath);
        detail.setVcsCodePath(vcsCodePath);
        detail.setVcsType(vcsType);
        detail.setDistPath(distPath);
        detail.setMachineCount(machines.size());
        detail.setMachines(machines);
        detail.setActions(actions);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void configAppProfile(ApplicationConfigRequest request) {
        Long appId = request.getAppId();
        Long profileId = request.getProfileId();
        // 检查代码仓库是否正确
        this.checkVcsLocalPath(request.getEnv());
        // 查询应用和环境
        Valid.notNull(applicationInfoDAO.selectById(appId), MessageConst.APP_ABSENT);
        Valid.notNull(applicationProfileDAO.selectById(profileId), MessageConst.PROFILE_ABSENT);
        // 配置环境变量
        this.configAppEnv(request.getEnv(), appId, profileId);
        // 配置机器
        applicationMachineService.deleteAppMachineByAppProfileId(appId, profileId);
        this.configAppMachines(request.getMachineIdList(), appId, profileId);
        // 配置部署
        applicationDeployActionService.deleteAppActionByAppProfileId(appId, profileId);
        this.configAppDeployAction(request.getActions(), appId, profileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAppProfileConfig(Long appId, Long profileId, Long syncProfileId) {
        // 查询应用和环境
        Valid.notNull(applicationInfoDAO.selectById(appId), MessageConst.APP_ABSENT);
        Valid.notNull(applicationProfileDAO.selectById(profileId), MessageConst.PROFILE_ABSENT);
        Valid.notNull(applicationProfileDAO.selectById(syncProfileId), MessageConst.PROFILE_ABSENT);
        // 检查环境是否已配置
        boolean isConfig = this.checkAppConfig(appId, profileId);
        Valid.isTrue(isConfig, MessageConst.APP_PROFILE_NOT_CONFIGURED);
        // 同步环境变量
        applicationEnvService.syncAppProfileEnv(appId, profileId, syncProfileId);
        // 同步机器
        applicationMachineService.syncAppProfileMachine(appId, profileId, syncProfileId);
        // 同步部署步骤
        applicationDeployActionService.syncAppProfileAction(appId, profileId, syncProfileId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyApplication(Long appId) {
        // 查询app
        ApplicationInfoDO app = Valid.notNull(applicationInfoDAO.selectById(appId), MessageConst.APP_ABSENT);
        app.setId(null);
        app.setAppName(app.getAppName() + " - " + Const.COPY);
        app.setCreateTime(null);
        app.setUpdateTime(null);
        applicationInfoDAO.insert(app);
        Long targetAppId = app.getId();
        // 复制环境变量
        applicationEnvService.copyAppEnv(appId, targetAppId);
        // 复制机器
        applicationMachineService.copyAppMachine(appId, targetAppId);
        // 复制部署步骤
        applicationDeployActionService.copyAppAction(appId, targetAppId);
    }

    @Override
    public boolean checkAppConfig(Long appId, Long profileId) {
        return applicationDeployActionService.selectAppProfileActionCount(appId, profileId) > 0;
    }

    @Override
    public ApplicationVcsInfoVO getVcsInfo(Long appId, Long profileId) {
        // 查询上线单
        ReleaseBillDO lastRelease = releaseInfoService.getLastReleaseBill(appId, profileId);
        // 查询vcs路径
        String path = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.VCS_ROOT_PATH.getKey());
        // 获取vcs实例
        try (Gits git = Gits.of(new File(path))) {
            ApplicationVcsInfoVO vcsInfo = new ApplicationVcsInfoVO();
            ApplicationVcsBranchVO branch;
            // 获取分支列表
            List<ApplicationVcsBranchVO> branches = Converts.toList(git.branchList(), ApplicationVcsBranchVO.class);
            if (lastRelease != null) {
                branch = branches.stream()
                        .filter(s -> s.getName().equals(lastRelease.getBranchName()))
                        .findFirst()
                        .orElseGet(() -> branches.get(0));
            } else {
                branch = branches.get(0);
            }
            branch.setIsDefault(Const.IS_DEFAULT);
            // 获取commit
            List<LogInfo> logList = git.logList(branch.getName(), Const.VCS_COMMIT_LIMIT);
            vcsInfo.setBranches(branches);
            vcsInfo.setCommits(Converts.toList(logList, ApplicationVcsCommitVO.class));
            return vcsInfo;
        }
    }

    @Override
    public List<ApplicationVcsBranchVO> getVcsBranchList(Long appId, Long profileId) {
        String path = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.VCS_ROOT_PATH.getKey());
        try (Gits git = Gits.of(new File(path))) {
            // 查询分支信息
            List<BranchInfo> branchList = git.branchList();
            return Converts.toList(branchList, ApplicationVcsBranchVO.class);
        }
    }

    @Override
    public List<ApplicationVcsCommitVO> getVcsCommitList(Long appId, Long profileId, String branchName) {
        String path = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.VCS_ROOT_PATH.getKey());
        try (Gits git = Gits.of(new File(path))) {
            // 查询提交信息
            List<LogInfo> logList = git.logList(branchName, Const.VCS_COMMIT_LIMIT);
            return Converts.toList(logList, ApplicationVcsCommitVO.class);
        }
    }

    /**
     * 检查app 版本控制工具路径
     *
     * @param env env
     */
    private void checkVcsLocalPath(ApplicationConfigEnvRequest env) {
        boolean codePathPresent = Files1.isDirectory(env.getVcsCodePath());
        Valid.isTrue(codePathPresent, MessageConst.CODE_PATH_ABSENT);
        File rootPath = new File(env.getVcsRootPath());
        boolean rootPathPresent = Files1.isDirectory(rootPath);
        Valid.isTrue(rootPathPresent, MessageConst.VCS_ROOT_PATH_ABSENT);
        Gits git = null;
        try {
            git = Gits.of(rootPath);
        } catch (Exception e) {
            throw Exceptions.invalidArgument(MessageConst.VCS_ROOT_PATH_UNCONNECTED, e);
        } finally {
            Streams.close(git);
        }
    }

    /**
     * 检查是否存在
     *
     * @param id   id
     * @param name name
     */
    private void checkNamePresent(Long id, String name) {
        LambdaQueryWrapper<ApplicationInfoDO> presentWrapper = new LambdaQueryWrapper<ApplicationInfoDO>()
                .ne(id != null, ApplicationInfoDO::getId, id)
                .eq(ApplicationInfoDO::getAppName, name);
        boolean present = DataQuery.of(applicationInfoDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.NAME_PRESENT);
    }

    /**
     * 配置app环境
     *
     * @param env       env
     * @param appId     appId
     * @param profileId profileId
     */
    private void configAppEnv(ApplicationConfigEnvRequest env, Long appId, Long profileId) {
        // 版本控制根目录
        ApplicationEnvRequest vcsRootPath = new ApplicationEnvRequest();
        vcsRootPath.setKey(ApplicationEnvAttr.VCS_ROOT_PATH.getKey());
        vcsRootPath.setValue(env.getVcsRootPath());
        vcsRootPath.setDescription(ApplicationEnvAttr.VCS_ROOT_PATH.getDescription());
        // 应用代码目录
        ApplicationEnvRequest vcsCodePath = new ApplicationEnvRequest();
        vcsCodePath.setKey(ApplicationEnvAttr.VCS_CODE_PATH.getKey());
        vcsCodePath.setValue(env.getVcsCodePath());
        vcsCodePath.setDescription(ApplicationEnvAttr.VCS_CODE_PATH.getDescription());
        // 版本管理工具
        ApplicationEnvRequest vcsType = new ApplicationEnvRequest();
        vcsType.setKey(ApplicationEnvAttr.VCS_TYPE.getKey());
        vcsType.setValue(env.getVcsType());
        vcsType.setDescription(ApplicationEnvAttr.VCS_TYPE.getDescription());
        // 构建产物目录
        ApplicationEnvRequest distPath = new ApplicationEnvRequest();
        distPath.setKey(ApplicationEnvAttr.DIST_PATH.getKey());
        distPath.setValue(env.getDistPath());
        distPath.setDescription(ApplicationEnvAttr.DIST_PATH.getDescription());
        // reduce
        List<ApplicationEnvRequest> envs = Lists.of(vcsRootPath, vcsCodePath, vcsType, distPath);
        envs.forEach(e -> {
            e.setAppId(appId);
            e.setProfileId(profileId);
        });
        envs.forEach(applicationEnvService::addAppEnv);
    }

    /**
     * 配置app机器
     *
     * @param machineIdList 机器列表
     * @param appId         appId
     * @param profileId     profileId
     */
    private void configAppMachines(List<Long> machineIdList, Long appId, Long profileId) {
        List<ApplicationMachineDO> list = machineIdList.stream()
                .map(i -> {
                    ApplicationMachineDO machine = new ApplicationMachineDO();
                    machine.setAppId(appId);
                    machine.setProfileId(profileId);
                    machine.setMachineId(i);
                    return machine;
                }).collect(Collectors.toList());
        // 检查
        for (Long machineId : machineIdList) {
            MachineInfoDO machine = machineInfoService.selectById(machineId);
            Valid.notNull(machine, MessageConst.INVALID_MACHINE);
        }
        list.forEach(applicationMachineDAO::insert);
    }

    /**
     * 配置app部署步骤
     *
     * @param actions   actions
     * @param appId     appId
     * @param profileId profileId
     */
    private void configAppDeployAction(List<ApplicationConfigDeployActionRequest> actions, Long appId, Long profileId) {
        // 新增
        for (ApplicationConfigDeployActionRequest action : actions) {
            ApplicationDeployActionDO entity = new ApplicationDeployActionDO();
            entity.setAppId(appId);
            entity.setProfileId(profileId);
            entity.setActionType(action.getType());
            entity.setActionName(action.getName());
            entity.setActionCommand(action.getCommand());
            applicationDeployActionDAO.insert(entity);
        }
    }

}
