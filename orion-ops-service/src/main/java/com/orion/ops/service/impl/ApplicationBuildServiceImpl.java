package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.*;
import com.orion.ops.consts.env.EnvConst;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.ApplicationBuildRequest;
import com.orion.ops.entity.vo.*;
import com.orion.ops.handler.build.BuildSessionHolder;
import com.orion.ops.handler.build.IBuilderProcessor;
import com.orion.ops.service.api.ApplicationActionService;
import com.orion.ops.service.api.ApplicationBuildService;
import com.orion.ops.service.api.ApplicationEnvService;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.collect.Maps;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.Files1;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 构建服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/3 14:14
 */
@Service("applicationBuildService")
public class ApplicationBuildServiceImpl implements ApplicationBuildService {

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private ApplicationBuildActionDAO applicationBuildActionDAO;

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Resource
    private ApplicationProfileDAO applicationProfileDAO;

    @Resource
    private ApplicationVcsDAO applicationVcsDAO;

    @Resource
    private ApplicationEnvService applicationEnvService;

    @Resource
    private ApplicationActionService applicationActionService;

    @Resource
    private MachineEnvService machineEnvService;

    @Resource
    private BuildSessionHolder buildSessionHolder;

    @Override
    public Long submitBuildTask(ApplicationBuildRequest request) {
        Long appId = request.getAppId();
        Long profileId = request.getProfileId();
        // 查询应用
        ApplicationInfoDO app = applicationInfoDAO.selectById(appId);
        Valid.notNull(app, MessageConst.APP_ABSENT);
        // 查询环境
        ApplicationProfileDO profile = applicationProfileDAO.selectById(profileId);
        Valid.notNull(profile, MessageConst.PROFILE_ABSENT);
        // 查询应用执行块
        List<ApplicationActionDO> actions = applicationActionService.getAppProfileActions(appId, profileId, StageType.BUILD.getType());
        Valid.notEmpty(actions, MessageConst.APP_PROFILE_NOT_CONFIGURED);
        UserDTO user = Currents.getUser();
        // 获取构建序列
        Integer buildSeq = applicationEnvService.getBuildSeqAndIncrement(appId, profileId);
        // 设置构建参数
        ApplicationBuildDO buildTask = new ApplicationBuildDO();
        buildTask.setAppId(appId);
        buildTask.setAppName(app.getAppName());
        buildTask.setAppTag(app.getAppTag());
        buildTask.setProfileId(profileId);
        buildTask.setProfileName(profile.getProfileName());
        buildTask.setProfileTag(profile.getProfileTag());
        buildTask.setBuildSeq(buildSeq);
        buildTask.setBranchName(request.getBranchName());
        buildTask.setCommitId(request.getCommitId());
        buildTask.setVcsId(app.getVcsId());
        buildTask.setBuildStatus(BuildStatus.WAIT.getStatus());
        buildTask.setDescription(request.getDescription());
        buildTask.setCreateUserId(user.getId());
        buildTask.setCreateUserName(user.getUsername());
        applicationBuildDAO.insert(buildTask);
        Long buildId = buildTask.getId();
        // 设置目录信息
        buildTask.setLogPath(PathBuilders.getBuildLogPath(buildId));
        String bundlePath = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.BUNDLE_PATH.getKey());
        buildTask.setBundlePath(PathBuilders.getBuildBundlePath(buildId) + "/" + Files1.getFileName(bundlePath));
        // 更新构建信息
        applicationBuildDAO.updateById(buildTask);
        // 检查是否包含命令
        boolean hasCommand = actions.stream()
                .map(ApplicationActionDO::getActionType)
                .anyMatch(ActionType.BUILD_COMMAND.getType()::equals);
        Map<String, String> env = Maps.newMap();
        if (hasCommand) {
            // 查询应用环境变量
            env.putAll(applicationEnvService.getAppProfileFullEnv(appId, profileId));
            // 查询机器环境变量
            env.putAll(machineEnvService.getFullMachineEnv(Const.HOST_MACHINE_ID));
            // 添加构建环境变量
            env.putAll(this.getBuildEnv(buildId, buildSeq, app.getVcsId(), request));
        }
        // 设置action
        for (ApplicationActionDO action : actions) {
            ApplicationBuildActionDO buildAction = new ApplicationBuildActionDO();
            buildAction.setBuildId(buildId);
            buildAction.setActionId(action.getAppId());
            buildAction.setActionName(action.getActionName());
            buildAction.setActionType(action.getActionType());
            if (ActionType.BUILD_COMMAND.equals(ActionType.of(action.getActionType()))) {
                buildAction.setActionCommand(Strings.format(action.getActionCommand(), EnvConst.SYMBOL, env));
            }
            buildAction.setRunStatus(ActionStatus.WAIT.getStatus());
            applicationBuildActionDAO.insert(buildAction);
            // 设置日志路径
            buildAction.setLogPath(PathBuilders.getBuildActionLogPath(buildId, buildAction.getId()));
            // 更新
            applicationBuildActionDAO.updateById(buildAction);
        }
        // 提交构建任务
        IBuilderProcessor.with(buildId).exec();
        return buildId;
    }

    @Override
    public DataGrid<ApplicationBuildVO> getBuildList(ApplicationBuildRequest request) {
        LambdaQueryWrapper<ApplicationBuildDO> wrapper = new LambdaQueryWrapper<ApplicationBuildDO>()
                .eq(ApplicationBuildDO::getProfileId, request.getProfileId())
                .eq(Objects.nonNull(request.getId()), ApplicationBuildDO::getId, request.getId())
                .eq(Objects.nonNull(request.getAppId()), ApplicationBuildDO::getAppId, request.getAppId())
                .eq(Objects.nonNull(request.getSeq()), ApplicationBuildDO::getBuildSeq, request.getSeq())
                .eq(Objects.nonNull(request.getStatus()), ApplicationBuildDO::getBuildStatus, request.getStatus())
                .eq(Const.ENABLE.equals(request.getOnlyMyself()), ApplicationBuildDO::getCreateUserId, Currents.getUserId())
                .like(Strings.isNotBlank(request.getDescription()), ApplicationBuildDO::getDescription, request.getDescription())
                .orderByDesc(ApplicationBuildDO::getUpdateTime);
        // 查询列表
        DataGrid<ApplicationBuildVO> dataGrid = DataQuery.of(applicationBuildDAO)
                .wrapper(wrapper)
                .page(request)
                .dataGrid(ApplicationBuildVO.class);
        // 查询版本信息
        Map<Long, ApplicationVcsDO> vcsCache = Maps.newMap();
        for (ApplicationBuildVO row : dataGrid) {
            Long vcsId = row.getVcsId();
            if (vcsId == null) {
                continue;
            }
            ApplicationVcsDO vcs = vcsCache.computeIfAbsent(vcsId, i -> applicationVcsDAO.selectById(vcsId));
            if (vcs != null) {
                row.setVcsName(vcs.getVcsName());
            }
        }
        return dataGrid;
    }

    @Override
    public ApplicationBuildVO getBuildDetail(Long id) {
        ApplicationBuildDO build = applicationBuildDAO.selectById(id);
        Valid.notNull(build, MessageConst.UNKNOWN_DATA);
        ApplicationBuildVO detail = Converts.to(build, ApplicationBuildVO.class);
        // 查询版本信息
        Optional.ofNullable(build.getVcsId())
                .map(applicationVcsDAO::selectById)
                .ifPresent(v -> detail.setVcsName(v.getVcsName()));
        // 查询action
        LambdaQueryWrapper<ApplicationBuildActionDO> actionWrapper = new LambdaQueryWrapper<ApplicationBuildActionDO>()
                .eq(ApplicationBuildActionDO::getBuildId, id)
                .orderByAsc(ApplicationBuildActionDO::getId);
        List<ApplicationBuildActionVO> actions = DataQuery.of(applicationBuildActionDAO)
                .wrapper(actionWrapper)
                .list(ApplicationBuildActionVO.class);
        detail.setActions(actions);
        return detail;
    }

    @Override
    public ApplicationBuildStatusVO getBuildStatus(Long id) {
        // 查询构建状态
        ApplicationBuildDO buildStatus = applicationBuildDAO.selectStatusInfoById(id);
        Valid.notNull(buildStatus, MessageConst.UNKNOWN_DATA);
        ApplicationBuildStatusVO status = Converts.to(buildStatus, ApplicationBuildStatusVO.class);
        // 查询操作状态
        List<ApplicationBuildActionDO> actionStatus = applicationBuildActionDAO.selectStatusInfoByBuildId(id);
        List<ApplicationBuildActionStatusVO> actions = Converts.toList(actionStatus, ApplicationBuildActionStatusVO.class);
        status.setActions(actions);
        return status;
    }

    @Override
    public List<ApplicationBuildStatusVO> getBuildStatusList(List<Long> buildIdList) {
        List<ApplicationBuildDO> buildList = applicationBuildDAO.selectStatusInfoByIdList(buildIdList);
        return Converts.toList(buildList, ApplicationBuildStatusVO.class);
    }

    @Override
    public void terminatedBuildTask(Long id) {
        // 获取数据
        ApplicationBuildDO build = applicationBuildDAO.selectById(id);
        Valid.notNull(build, MessageConst.UNKNOWN_DATA);
        // 检查状态
        ApplicationBuildDO update;
        switch (BuildStatus.of(build.getBuildStatus())) {
            case WAIT:
            case RUNNABLE:
                // 获取实例
                IBuilderProcessor session = buildSessionHolder.getSession(id);
                if (session == null) {
                    update = new ApplicationBuildDO();
                    update.setId(id);
                    update.setBuildStatus(BuildStatus.TERMINATED.getStatus());
                    update.setBuildEndTime(new Date());
                    applicationBuildDAO.updateById(update);
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
    public Integer deleteBuildTask(Long id) {
        // 获取数据
        ApplicationBuildDO build = applicationBuildDAO.selectById(id);
        Valid.notNull(build, MessageConst.UNKNOWN_DATA);
        if (BuildStatus.RUNNABLE.getStatus().equals(build.getBuildStatus())) {
            throw Exceptions.argument(MessageConst.INVALID_STATUS);
        }
        // 删除主表
        int effect = applicationBuildDAO.deleteById(id);
        // 删除详情
        Wrapper<ApplicationBuildActionDO> actionWrapper = new LambdaQueryWrapper<ApplicationBuildActionDO>()
                .eq(ApplicationBuildActionDO::getBuildId, id);
        effect += applicationBuildActionDAO.delete(actionWrapper);
        return effect;
    }

    @Override
    public Long rebuild(Long id) {
        // 查询构建
        ApplicationBuildDO build = applicationBuildDAO.selectById(id);
        Valid.notNull(build, MessageConst.UNKNOWN_DATA);
        // 重新提交
        ApplicationBuildRequest request = new ApplicationBuildRequest();
        request.setAppId(build.getAppId());
        request.setProfileId(build.getProfileId());
        request.setBranchName(build.getBranchName());
        request.setCommitId(build.getCommitId());
        request.setDescription(build.getDescription());
        return this.submitBuildTask(request);
    }

    @Override
    public ApplicationBuildDO selectById(Long id) {
        return applicationBuildDAO.selectById(id);
    }

    @Override
    public List<ApplicationBuildActionDO> selectActionById(Long id) {
        LambdaQueryWrapper<ApplicationBuildActionDO> wrapper = new LambdaQueryWrapper<ApplicationBuildActionDO>()
                .eq(ApplicationBuildActionDO::getBuildId, id)
                .orderByAsc(ApplicationBuildActionDO::getId);
        return applicationBuildActionDAO.selectList(wrapper);
    }

    @Override
    public void updateById(ApplicationBuildDO record) {
        if (record.getUpdateTime() == null) {
            record.setUpdateTime(new Date());
        }
        applicationBuildDAO.updateById(record);
    }

    @Override
    public void updateActionById(ApplicationBuildActionDO record) {
        if (record.getUpdateTime() == null) {
            record.setUpdateTime(new Date());
        }
        applicationBuildActionDAO.updateById(record);
    }

    @Override
    public String getBuildLogPath(Long id) {
        return Optional.ofNullable(applicationBuildDAO.selectById(id))
                .map(ApplicationBuildDO::getLogPath)
                .filter(Strings::isNotBlank)
                .map(s -> Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), s))
                .orElse(null);
    }

    @Override
    public String getBuildActionLogPath(Long id) {
        return Optional.ofNullable(applicationBuildActionDAO.selectById(id))
                .map(ApplicationBuildActionDO::getLogPath)
                .filter(Strings::isNotBlank)
                .map(s -> Files1.getPath(MachineEnvAttr.LOG_PATH.getValue(), s))
                .orElse(null);
    }

    @Override
    public String getBuildBundlePath(Long id) {
        return Optional.ofNullable(applicationBuildDAO.selectById(id))
                .map(ApplicationBuildDO::getBundlePath)
                .filter(Strings::isNotBlank)
                .map(s -> Files1.getPath(MachineEnvAttr.DIST_PATH.getValue(), s))
                .orElse(null);
    }

    @Override
    public List<ApplicationBuildReleaseListVO> getBuildReleaseList(Long appId, Long profileId) {
        List<ApplicationBuildDO> list = applicationBuildDAO.selectBuildReleaseList(appId, profileId, Const.BUILD_RELEASE_LIMIT);
        return Converts.toList(list, ApplicationBuildReleaseListVO.class);
    }

    /**
     * 获取构建环境变量
     *
     * @param buildId  buildId
     * @param buildSeq buildSeq
     * @param vcsId    vcsId
     * @param request  request
     * @return env
     */
    private MutableLinkedHashMap<String, String> getBuildEnv(Long buildId, Integer buildSeq,
                                                             Long vcsId, ApplicationBuildRequest request) {
        // 设置变量
        MutableLinkedHashMap<String, String> env = Maps.newMutableLinkedMap();
        env.put(EnvConst.BUILD_ID, buildId + Strings.EMPTY);
        env.put(EnvConst.BUILD_SEQ, buildSeq + Strings.EMPTY);
        env.put(EnvConst.BRANCH, request.getBranchName() + Strings.EMPTY);
        env.put(EnvConst.COMMIT, request.getCommitId() + Strings.EMPTY);
        if (vcsId != null) {
            env.put(EnvConst.VCS_HOME, Files1.getPath(MachineEnvAttr.VCS_PATH.getValue(), vcsId + "/" + buildId));
        }
        // 设置前缀
        MutableLinkedHashMap<String, String> fullEnv = Maps.newMutableLinkedMap();
        env.forEach((k, v) -> {
            fullEnv.put(EnvConst.BUILD_PREFIX + k, v);
        });
        return fullEnv;
    }

}
