package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.EnvConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.app.ActionType;
import com.orion.ops.consts.app.BuildStatus;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.ApplicationBuildRequest;
import com.orion.ops.entity.vo.ApplicationBuildActionVO;
import com.orion.ops.entity.vo.ApplicationBuildStatusVO;
import com.orion.ops.entity.vo.ApplicationBuildVO;
import com.orion.ops.handler.build.IBuilderProcessor;
import com.orion.ops.service.api.ApplicationActionService;
import com.orion.ops.service.api.ApplicationBuildService;
import com.orion.ops.service.api.ApplicationEnvService;
import com.orion.ops.service.api.MachineEnvService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Valid;
import com.orion.utils.Strings;
import com.orion.utils.collect.Maps;
import com.orion.utils.convert.Converts;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    private RedisTemplate<String, String> redisTemplate;

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
        buildTask.setProfileTag(app.getAppTag());
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
        buildTask.setDistPath(PathBuilders.getBuildDistPath(buildId));
        // 更新构建信息
        applicationBuildDAO.updateById(buildTask);
        // 检查是否包含命令
        boolean hasCommand = actions.stream()
                .map(ApplicationActionDO::getActionType)
                .anyMatch(ActionType.BUILD_HOST_COMMAND.getType()::equals);
        Map<String, String> env = Maps.newMap();
        if (hasCommand) {
            // 查询应用环境变量
            env.putAll(applicationEnvService.getAppProfileFullEnv(appId, profileId));
            // 查询机器环境变量
            env.putAll(machineEnvService.getFullMachineEnv(Const.HOST_MACHINE_ID, EnvConst.MACHINE_PREFIX));
        }
        // 设置action
        for (ApplicationActionDO action : actions) {
            ApplicationBuildActionDO buildAction = new ApplicationBuildActionDO();
            buildAction.setBuildId(buildId);
            buildAction.setActionId(action.getAppId());
            buildAction.setActionName(action.getActionName());
            buildAction.setActionType(action.getActionType());
            if (ActionType.BUILD_HOST_COMMAND.equals(ActionType.of(action.getActionType()))) {
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
                .eq(Objects.nonNull(request.getBuildSeq()), ApplicationBuildDO::getBuildSeq, request.getBuildSeq())
                .eq(Objects.nonNull(request.getStatus()), ApplicationBuildDO::getBuildStatus, request.getStatus())
                .eq(Const.ENABLE.equals(request.getOnlyMyself()), ApplicationBuildDO::getCreateUserId, Currents.getUserId())
                .like(Strings.isNotBlank(request.getDescription()), ApplicationBuildDO::getDescription, request.getDescription())
                .orderByDesc(ApplicationBuildDO::getId);
        // 查询列表
        DataGrid<ApplicationBuildVO> dataGrid = DataQuery.of(applicationBuildDAO)
                .wrapper(wrapper)
                .page(request)
                .dataGrid(ApplicationBuildVO.class);
        // 查询版本信息
        Map<Long, ApplicationVcsDO> vcsCache = Maps.newMap();
        for (ApplicationBuildVO row : dataGrid.getRows()) {
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
        return null;
    }

    @Override
    public void terminatedBuildTask(Long id) {

    }

    @Override
    public void rebuild(Long id) {

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
        applicationBuildDAO.updateById(record);
    }

    @Override
    public void updateActionById(ApplicationBuildActionDO record) {
        applicationBuildActionDAO.updateById(record);
    }

}
