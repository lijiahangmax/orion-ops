package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.collect.MutableLinkedHashMap;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.io.Files1;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.*;
import com.orion.ops.consts.env.EnvConst;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.event.EventParamsHolder;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.ApplicationBuildRequest;
import com.orion.ops.entity.vo.*;
import com.orion.ops.handler.app.build.BuildSessionHolder;
import com.orion.ops.handler.app.machine.BuildMachineProcessor;
import com.orion.ops.handler.app.machine.IMachineProcessor;
import com.orion.ops.service.api.*;
import com.orion.ops.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 构建服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/3 14:14
 */
@Slf4j
@Service("applicationBuildService")
public class ApplicationBuildServiceImpl implements ApplicationBuildService {

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private ApplicationActionLogDAO applicationActionLogDAO;

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
    private ApplicationActionLogService applicationActionLogService;

    @Resource
    private MachineEnvService machineEnvService;

    @Resource
    private SystemEnvService systemEnvService;

    @Resource
    private BuildSessionHolder buildSessionHolder;

    @Override
    public Long submitBuildTask(ApplicationBuildRequest request, boolean execute) {
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
        String bundlePathEnv = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.BUNDLE_PATH.getKey());
        String bundlePathReal = PathBuilders.getBuildBundlePath(buildId) + "/" + Files1.getFileName(bundlePathEnv);
        buildTask.setBundlePath(bundlePathReal);
        // 更新构建信息
        applicationBuildDAO.updateById(buildTask);
        // 检查是否包含环境变量命令
        final boolean hasEnvCommand = actions.stream()
                .filter(s -> ActionType.BUILD_COMMAND.getType().equals(s.getActionType()))
                .map(ApplicationActionDO::getActionCommand)
                .filter(Strings::isNotBlank)
                .anyMatch(s -> s.contains(EnvConst.SYMBOL));

        Map<String, String> env = Maps.newMap();
        if (hasEnvCommand) {
            // 查询应用环境变量
            env.putAll(applicationEnvService.getAppProfileFullEnv(appId, profileId));
            // 查询机器环境变量
            env.putAll(machineEnvService.getFullMachineEnv(Const.HOST_MACHINE_ID));
            // 查询系统环境变量
            env.putAll(systemEnvService.getFullSystemEnv());
            // 添加构建环境变量
            env.putAll(this.getBuildEnv(buildId, buildSeq, app.getVcsId(), bundlePathReal, request));
        }
        // 设置action
        for (ApplicationActionDO action : actions) {
            ApplicationActionLogDO actionLog = new ApplicationActionLogDO();
            actionLog.setRelId(buildId);
            actionLog.setStageType(StageType.BUILD.getType());
            actionLog.setMachineId(Const.HOST_MACHINE_ID);
            actionLog.setActionId(action.getAppId());
            actionLog.setActionName(action.getActionName());
            actionLog.setActionType(action.getActionType());
            if (ActionType.BUILD_COMMAND.equals(ActionType.of(action.getActionType()))) {
                actionLog.setActionCommand(Strings.format(action.getActionCommand(), EnvConst.SYMBOL, env));
            }
            actionLog.setRunStatus(ActionStatus.WAIT.getStatus());
            applicationActionLogDAO.insert(actionLog);
            // 设置日志路径
            actionLog.setLogPath(PathBuilders.getBuildActionLogPath(buildId, actionLog.getId()));
            // 更新
            applicationActionLogDAO.updateById(actionLog);
        }
        // 提交构建任务
        log.info("提交应用构建任务 buildId: {}", buildId);
        if (execute) {
            new BuildMachineProcessor(buildId).exec();
        }
        // 设置日志参数
        EventParamsHolder.addParams(buildTask);
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
                .orderByDesc(ApplicationBuildDO::getId);
        // 查询列表
        DataGrid<ApplicationBuildVO> dataGrid = DataQuery.of(applicationBuildDAO)
                .page(request)
                .wrapper(wrapper)
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
        List<ApplicationActionLogVO> actions = applicationActionLogService.getActionLogsByRelId(id, StageType.BUILD);
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
        List<ApplicationActionLogDO> actions = applicationActionLogDAO.selectStatusInfoByRelId(id, StageType.BUILD.getType());
        status.setActions(Converts.toList(actions, ApplicationActionStatusVO.class));
        return status;
    }

    @Override
    public List<ApplicationBuildStatusVO> getBuildStatusList(List<Long> buildIdList) {
        List<ApplicationBuildDO> buildList = applicationBuildDAO.selectStatusInfoByIdList(buildIdList);
        return Converts.toList(buildList, ApplicationBuildStatusVO.class);
    }

    @Override
    public void terminateBuildTask(Long id) {
        // 获取数据
        ApplicationBuildDO build = applicationBuildDAO.selectById(id);
        Valid.notNull(build, MessageConst.UNKNOWN_DATA);
        // 检查状态
        Valid.isTrue(BuildStatus.RUNNABLE.getStatus().equals(build.getBuildStatus()), MessageConst.ILLEGAL_STATUS);
        // 获取实例
        IMachineProcessor session = buildSessionHolder.getSession(id);
        Valid.notNull(session, MessageConst.SESSION_PRESENT);
        // 调用终止
        session.terminate();
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.BUILD_SEQ, build.getBuildSeq());
        EventParamsHolder.addParam(EventKeys.APP_NAME, build.getAppName());
        EventParamsHolder.addParam(EventKeys.PROFILE_NAME, build.getProfileName());
    }

    @Override
    public void writeBuildTask(Long id, String command) {
        // 获取数据
        ApplicationBuildDO build = applicationBuildDAO.selectById(id);
        Valid.notNull(build, MessageConst.UNKNOWN_DATA);
        // 检查状态
        Valid.isTrue(BuildStatus.RUNNABLE.getStatus().equals(build.getBuildStatus()), MessageConst.ILLEGAL_STATUS);
        // 获取实例
        IMachineProcessor session = buildSessionHolder.getSession(id);
        Valid.notNull(session, MessageConst.SESSION_PRESENT);
        // 写入命令
        session.write(command);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteBuildTask(List<Long> idList) {
        // 获取数据
        List<ApplicationBuildDO> buildList = applicationBuildDAO.selectBatchIds(idList);
        Valid.notEmpty(buildList, MessageConst.UNKNOWN_DATA);
        boolean canDelete = buildList.stream()
                .map(ApplicationBuildDO::getBuildStatus)
                .noneMatch(s -> BuildStatus.WAIT.getStatus().equals(s) || BuildStatus.RUNNABLE.getStatus().equals(s));
        Valid.isTrue(canDelete, MessageConst.ILLEGAL_STATUS);
        // 删除主表
        int effect = applicationBuildDAO.deleteBatchIds(idList);
        // 删除详情
        effect += applicationActionLogService.deleteByRelIdList(idList, StageType.BUILD);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, idList.size());
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
        return this.submitBuildTask(request, true);
    }

    @Override
    public ApplicationBuildDO selectById(Long id) {
        return applicationBuildDAO.selectById(id);
    }

    @Override
    public String getBuildLogPath(Long id) {
        return Optional.ofNullable(applicationBuildDAO.selectById(id))
                .map(ApplicationBuildDO::getLogPath)
                .filter(Strings::isNotBlank)
                .map(s -> Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), s))
                .orElse(null);
    }

    @Override
    public String getBuildBundlePath(Long id) {
        return Optional.ofNullable(applicationBuildDAO.selectById(id))
                .map(ApplicationBuildDO::getBundlePath)
                .filter(Strings::isNotBlank)
                .map(s -> Files1.getPath(SystemEnvAttr.DIST_PATH.getValue(), s))
                .orElse(null);
    }

    @Override
    public String checkBuildBundlePath(ApplicationBuildDO build) {
        // 构建产物
        String buildBundlePath = build.getBundlePath();
        File bundleFile = new File(Files1.getPath(SystemEnvAttr.DIST_PATH.getValue(), buildBundlePath));
        Valid.isTrue(bundleFile.exists(), MessageConst.BUNDLE_FILE_ABSENT);
        if (bundleFile.isFile()) {
            return buildBundlePath;
        }
        // 如果是文件夹则需要检查传输文件是文件夹还是压缩文件
        String transferValueValue = applicationEnvService.getAppEnvValue(build.getAppId(), build.getProfileId(), ApplicationEnvAttr.TRANSFER_FILE_TYPE.getKey());
        if (TransferFileType.ZIP.equals(TransferFileType.of(transferValueValue))) {
            buildBundlePath = build.getBundlePath() + "." + Const.SUFFIX_ZIP;
            bundleFile = new File(Files1.getPath(SystemEnvAttr.DIST_PATH.getValue(), buildBundlePath));
            Valid.isTrue(bundleFile.exists(), MessageConst.BUNDLE_ZIP_FILE_ABSENT);
        }
        return buildBundlePath;
    }

    @Override
    public List<ApplicationBuildReleaseListVO> getBuildReleaseList(Long appId, Long profileId) {
        List<ApplicationBuildDO> list = applicationBuildDAO.selectBuildReleaseList(appId, profileId, Const.BUILD_RELEASE_LIMIT);
        return Converts.toList(list, ApplicationBuildReleaseListVO.class);
    }

    /**
     * 获取构建环境变量
     *
     * @param buildId        buildId
     * @param buildSeq       buildSeq
     * @param vcsId          vcsId
     * @param bundlePathReal bundlePathReal
     * @param request        request
     * @return env
     */
    private MutableLinkedHashMap<String, String> getBuildEnv(Long buildId, Integer buildSeq,
                                                             Long vcsId, String bundlePathReal, ApplicationBuildRequest request) {
        // 设置变量
        MutableLinkedHashMap<String, String> env = Maps.newMutableLinkedMap();
        env.put(EnvConst.BUILD_PREFIX + EnvConst.BUILD_ID, buildId + Strings.EMPTY);
        env.put(EnvConst.BUILD_PREFIX + EnvConst.BUILD_SEQ, buildSeq + Strings.EMPTY);
        env.put(EnvConst.BUILD_PREFIX + EnvConst.BRANCH, request.getBranchName() + Strings.EMPTY);
        env.put(EnvConst.BUILD_PREFIX + EnvConst.COMMIT, request.getCommitId() + Strings.EMPTY);
        if (vcsId != null) {
            env.put(EnvConst.BUILD_PREFIX + EnvConst.VCS_HOME, Files1.getPath(SystemEnvAttr.VCS_PATH.getValue(), vcsId + "/" + buildId));
            env.put(EnvConst.BUILD_PREFIX + EnvConst.VCS_EVENT_HOME, Utils.getVcsEventDir(vcsId));
        }
        env.put(EnvConst.BUILD_PREFIX + EnvConst.BUNDLE_PATH, bundlePathReal);
        return env;
    }

}
