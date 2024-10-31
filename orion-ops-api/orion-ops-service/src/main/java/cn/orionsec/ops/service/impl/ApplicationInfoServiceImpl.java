/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.service.impl;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.collect.Lists;
import cn.orionsec.kit.lang.utils.collect.Maps;
import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.app.*;
import cn.orionsec.ops.constant.common.ExceptionHandlerType;
import cn.orionsec.ops.constant.common.SerialType;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.dao.ApplicationInfoDAO;
import cn.orionsec.ops.dao.ApplicationProfileDAO;
import cn.orionsec.ops.dao.ApplicationRepositoryDAO;
import cn.orionsec.ops.entity.domain.*;
import cn.orionsec.ops.entity.request.app.ApplicationConfigRequest;
import cn.orionsec.ops.entity.request.app.ApplicationInfoRequest;
import cn.orionsec.ops.entity.vo.app.*;
import cn.orionsec.ops.service.api.*;
import cn.orionsec.ops.utils.DataQuery;
import cn.orionsec.ops.utils.EventParamsHolder;
import cn.orionsec.ops.utils.Utils;
import cn.orionsec.ops.utils.Valid;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
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
    private ApplicationRepositoryDAO applicationRepositoryDAO;

    @Resource
    private ApplicationMachineService applicationMachineService;

    @Resource
    private ApplicationProfileDAO applicationProfileDAO;

    @Resource
    private ApplicationEnvService applicationEnvService;

    @Resource
    private MachineInfoService machineInfoService;

    @Resource
    private ApplicationActionService applicationActionService;

    @Resource
    private ApplicationPipelineDetailService applicationPipelineDetailService;

    @Override
    public Long insertApp(ApplicationInfoRequest request) {
        String name = request.getName();
        String tag = request.getTag();
        // 名称重复检查
        this.checkNamePresent(null, name);
        // 标识重复检查
        this.checkTagPresent(null, tag);
        // 插入
        ApplicationInfoDO insert = new ApplicationInfoDO();
        insert.setAppName(name);
        insert.setAppTag(tag);
        insert.setRepoId(request.getRepoId());
        insert.setDescription(request.getDescription());
        insert.setAppSort(this.getNextSort());
        applicationInfoDAO.insert(insert);
        // 设置日志参数
        EventParamsHolder.addParams(insert);
        return insert.getId();
    }

    @Override
    public Integer updateApp(ApplicationInfoRequest request) {
        Long id = request.getId();
        String name = request.getName();
        String tag = request.getTag();
        // 查询应用
        ApplicationInfoDO app = Valid.notNull(applicationInfoDAO.selectById(id), MessageConst.APP_ABSENT);
        // 名称重复检查
        this.checkNamePresent(id, name);
        // 标识重复检查
        this.checkTagPresent(id, tag);
        // 更新
        ApplicationInfoDO update = new ApplicationInfoDO();
        update.setId(id);
        update.setAppName(name);
        update.setAppTag(tag);
        update.setRepoId(request.getRepoId());
        update.setDescription(request.getDescription());
        update.setUpdateTime(new Date());
        int effect = applicationInfoDAO.updateById(update);
        // 设置日志参数
        EventParamsHolder.addParams(update);
        EventParamsHolder.addParam(EventKeys.NAME, app.getAppName());
        return effect;
    }

    @Override
    public Integer updateAppSort(Long id, boolean increment) {
        // 查询原来的排序
        ApplicationInfoDO app = applicationInfoDAO.selectById(id);
        Valid.notNull(app, MessageConst.APP_ABSENT);
        Integer beforeSort = app.getAppSort();
        // 查询下一个排序
        LambdaQueryWrapper<ApplicationInfoDO> wrapper;
        if (increment) {
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
        updateSwap.setRepoId(swapApp.getRepoId());
        updateSwap.setAppSort(beforeSort);
        applicationInfoDAO.updateById(updateSwap);
        // 更新
        Integer afterSort = swapApp.getAppSort();
        if (afterSort.equals(beforeSort)) {
            if (increment) {
                afterSort -= 1;
            } else {
                afterSort += 1;
            }
        }
        ApplicationInfoDO updateTarget = new ApplicationInfoDO();
        updateTarget.setId(id);
        updateTarget.setRepoId(app.getRepoId());
        updateTarget.setAppSort(afterSort);
        return applicationInfoDAO.updateById(updateTarget);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteApp(Long id) {
        // 查询应用
        ApplicationInfoDO app = Valid.notNull(applicationInfoDAO.selectById(id), MessageConst.APP_ABSENT);
        int effect = 0;
        // 删除应用
        effect += applicationInfoDAO.deleteById(id);
        // 删除环境变量
        effect += applicationEnvService.deleteAppProfileEnvByAppProfileId(id, null);
        // 删除机器
        effect += applicationMachineService.deleteAppMachineByAppProfileId(id, null);
        // 删除构建发布流程
        effect += applicationActionService.deleteAppActionByAppProfileId(id, null);
        // 删除应用流水线
        effect += applicationPipelineDetailService.deleteByAppId(id);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, app.getAppName());
        return effect;
    }

    @Override
    public DataGrid<ApplicationInfoVO> listApp(ApplicationInfoRequest request) {
        LambdaQueryWrapper<ApplicationInfoDO> wrapper = new LambdaQueryWrapper<ApplicationInfoDO>()
                .eq(Objects.nonNull(request.getId()), ApplicationInfoDO::getId, request.getId())
                .like(!Strings.isBlank(request.getName()), ApplicationInfoDO::getAppName, request.getName())
                .like(!Strings.isBlank(request.getTag()), ApplicationInfoDO::getAppTag, request.getTag())
                .like(!Strings.isBlank(request.getDescription()), ApplicationInfoDO::getDescription, request.getDescription())
                .orderByAsc(ApplicationInfoDO::getAppSort)
                .orderByAsc(ApplicationInfoDO::getId);
        // 查询应用
        DataGrid<ApplicationInfoVO> appList = DataQuery.of(applicationInfoDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(ApplicationInfoVO.class);
        // 设置版本仓库id
        List<Long> repoIdList = appList.stream().map(ApplicationInfoVO::getRepoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!repoIdList.isEmpty()) {
            applicationRepositoryDAO.selectNameByIdList(repoIdList).forEach(v -> {
                appList.stream().filter(s -> v.getId().equals(s.getRepoId()))
                        .forEach(s -> s.setRepoName(v.getRepoName()));
            });
        }
        Long profileId = request.getProfileId();
        if (appList.isEmpty() || profileId == null) {
            return appList;
        }
        // 设置配置状态
        List<Long> appIdList = appList.stream()
                .map(ApplicationInfoVO::getId)
                .collect(Collectors.toList());
        Map<Long, Boolean> configMap = applicationActionService.getAppIsConfig(profileId, appIdList);
        for (ApplicationInfoVO app : appList) {
            app.setIsConfig(configMap.get(app.getId()) ? Const.CONFIGURED : Const.NOT_CONFIGURED);
        }
        // 查询机器
        if (Const.ENABLE.equals(request.getQueryMachine())) {
            Map<Long, MachineInfoDO> machineCache = Maps.newMap();
            for (ApplicationInfoVO app : appList) {
                List<ApplicationMachineDO> appMachines = applicationMachineService.getAppProfileMachineList(app.getId(), profileId);
                if (appMachines.size() == 0) {
                    app.setMachines(Lists.empty());
                    continue;
                }
                List<ApplicationMachineVO> machines = Converts.toList(appMachines, ApplicationMachineVO.class);
                for (ApplicationMachineVO machine : machines) {
                    MachineInfoDO machineInfo = machineCache.computeIfAbsent(machine.getMachineId(), m -> machineInfoService.selectById(m));
                    if (machineInfo != null) {
                        machine.setMachineName(machineInfo.getMachineName());
                        machine.setMachineTag(machineInfo.getMachineTag());
                        machine.setMachineHost(machineInfo.getMachineHost());
                    }
                }
                app.setMachines(machines);
            }
        }
        return appList;
    }

    @Override
    public List<ApplicationMachineVO> getAppMachines(Long appId, Long profileId) {
        List<ApplicationMachineDO> appMachines = applicationMachineService.getAppProfileMachineList(appId, profileId);
        if (appMachines.size() == 0) {
            return Lists.empty();
        }
        List<ApplicationMachineVO> machines = Converts.toList(appMachines, ApplicationMachineVO.class);
        for (ApplicationMachineVO machine : machines) {
            MachineInfoDO machineInfo = machineInfoService.selectById(machine.getMachineId());
            if (machineInfo != null) {
                machine.setMachineName(machineInfo.getMachineName());
                machine.setMachineTag(machineInfo.getMachineTag());
                machine.setMachineHost(machineInfo.getMachineHost());
            }
        }
        return machines;
    }

    @Override
    public ApplicationDetailVO getAppDetail(Long appId, Long profileId) {
        // 查询应用
        ApplicationInfoDO app = applicationInfoDAO.selectById(appId);
        Valid.notNull(app, MessageConst.APP_ABSENT);
        ApplicationDetailVO detail = Converts.to(app, ApplicationDetailVO.class);
        // 查询仓库
        Optional.ofNullable(app.getRepoId())
                .map(applicationRepositoryDAO::selectById)
                .map(ApplicationRepositoryDO::getRepoName)
                .ifPresent(detail::setRepoName);
        // 没传环境id直接返回
        if (profileId == null) {
            return detail;
        }
        // 查询环境
        ApplicationProfileDO profile = applicationProfileDAO.selectById(profileId);
        Valid.notNull(profile, MessageConst.PROFILE_ABSENT);
        // 查询环境变量
        String bundlePath = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.BUNDLE_PATH.getKey());
        String transferPath = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.TRANSFER_PATH.getKey());
        String transferMode = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.TRANSFER_MODE.getKey());
        String transferFileType = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.TRANSFER_FILE_TYPE.getKey());
        String releaseSerial = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.RELEASE_SERIAL.getKey());
        String exceptionHandler = applicationEnvService.getAppEnvValue(appId, profileId, ApplicationEnvAttr.EXCEPTION_HANDLER.getKey());
        // 查询发布机器
        List<ApplicationMachineVO> machines = applicationMachineService.getAppProfileMachineDetail(appId, profileId);
        // 查询发布流程
        List<ApplicationActionDO> actions = applicationActionService.getAppProfileActions(appId, profileId, null);
        // 组装数据
        ApplicationConfigEnvVO env = new ApplicationConfigEnvVO();
        env.setBundlePath(bundlePath);
        env.setTransferPath(transferPath);
        env.setTransferMode(TransferMode.of(transferMode).getValue());
        env.setTransferFileType(TransferFileType.of(transferFileType).getValue());
        env.setReleaseSerial(SerialType.of(releaseSerial).getType());
        env.setExceptionHandler(ExceptionHandlerType.of(exceptionHandler).getType());
        List<ApplicationActionVO> buildActions = actions.stream()
                .filter(s -> ActionType.isBuildAction(s.getActionType()))
                .map(s -> Converts.to(s, ApplicationActionVO.class))
                .collect(Collectors.toList());
        List<ApplicationActionVO> releaseActions = actions.stream()
                .filter(s -> ActionType.isReleaseAction(s.getActionType()))
                .map(s -> Converts.to(s, ApplicationActionVO.class))
                .collect(Collectors.toList());
        detail.setEnv(env);
        detail.setReleaseMachines(machines);
        detail.setBuildActions(buildActions);
        detail.setReleaseActions(releaseActions);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void configAppProfile(ApplicationConfigRequest request) {
        Long appId = request.getAppId();
        Long profileId = request.getProfileId();
        // 查询应用和环境
        ApplicationInfoDO app = Valid.notNull(applicationInfoDAO.selectById(appId), MessageConst.APP_ABSENT);
        ApplicationProfileDO profile = Valid.notNull(applicationProfileDAO.selectById(profileId), MessageConst.PROFILE_ABSENT);
        StageType stageType = StageType.of(request.getStageType());
        if (StageType.RELEASE.equals(stageType)) {
            // 配置机器
            applicationMachineService.configAppMachines(request);
        }
        // 配置环境变量
        applicationEnvService.configAppEnv(request);
        // 配置发布
        applicationActionService.configAppAction(request);
        // 设置日志参数
        EventParamsHolder.addParams(request);
        EventParamsHolder.addParam(EventKeys.APP_NAME, app.getAppName());
        EventParamsHolder.addParam(EventKeys.PROFILE_NAME, profile.getProfileName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAppProfileConfig(Long appId, Long profileId, List<Long> targetProfileIdList) {
        // 查询应用和环境
        ApplicationInfoDO app = Valid.notNull(applicationInfoDAO.selectById(appId), MessageConst.APP_ABSENT);
        Valid.notNull(applicationProfileDAO.selectById(profileId), MessageConst.PROFILE_ABSENT);
        // 检查环境是否已配置
        boolean isConfig = this.checkAppConfig(appId, profileId);
        Valid.isTrue(isConfig, MessageConst.APP_PROFILE_NOT_CONFIGURED);
        for (Long targetProfileId : targetProfileIdList) {
            // 同步环境变量
            applicationEnvService.syncAppEnv(Const.L_N_1, appId, profileId, Lists.singleton(targetProfileId));
            // 同步机器
            applicationMachineService.syncAppProfileMachine(appId, profileId, targetProfileId);
            // 同步发布流程
            applicationActionService.syncAppProfileAction(appId, profileId, targetProfileId);
        }
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.NAME, app.getAppName());
        EventParamsHolder.addParam(EventKeys.COUNT, targetProfileIdList.size());
        EventParamsHolder.addParam(EventKeys.APP_ID, appId);
        EventParamsHolder.addParam(EventKeys.PROFILE_ID, profileId);
        EventParamsHolder.addParam(EventKeys.ID_LIST, targetProfileIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyApplication(Long appId) {
        // 查询app
        ApplicationInfoDO app = Valid.notNull(applicationInfoDAO.selectById(appId), MessageConst.APP_ABSENT);
        String beforeName = app.getAppName();
        app.setId(null);
        String copySuffix = Utils.getRandomSuffix();
        app.setAppName(beforeName + copySuffix);
        app.setAppTag(app.getAppTag() + copySuffix);
        app.setAppSort(this.getNextSort());
        app.setCreateTime(null);
        app.setUpdateTime(null);
        applicationInfoDAO.insert(app);
        Long targetAppId = app.getId();
        // 复制环境变量
        applicationEnvService.copyAppEnv(appId, targetAppId);
        // 复制机器
        applicationMachineService.copyAppMachine(appId, targetAppId);
        // 复制发布流程
        applicationActionService.copyAppAction(appId, targetAppId);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, appId);
        EventParamsHolder.addParam(EventKeys.NAME, beforeName);
    }

    @Override
    public boolean checkAppConfig(Long appId, Long profileId) {
        return applicationActionService.getAppProfileActionCount(appId, profileId, StageType.BUILD.getType()) > 0
                && applicationActionService.getAppProfileActionCount(appId, profileId, StageType.RELEASE.getType()) > 0;
    }

    @Override
    public Integer getNextSort() {
        Wrapper<ApplicationInfoDO> wrapper = new LambdaQueryWrapper<ApplicationInfoDO>()
                .orderByDesc(ApplicationInfoDO::getAppSort)
                .last(Const.LIMIT_1);
        return Optional.ofNullable(applicationInfoDAO.selectOne(wrapper))
                .map(ApplicationInfoDO::getAppSort)
                .map(s -> s + Const.N_10)
                .orElse(Const.N_10);
    }

    /**
     * 检查名称是否存在
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
     * 检查标签是否存在
     *
     * @param id  id
     * @param tag tag
     */
    private void checkTagPresent(Long id, String tag) {
        LambdaQueryWrapper<ApplicationInfoDO> presentWrapper = new LambdaQueryWrapper<ApplicationInfoDO>()
                .ne(id != null, ApplicationInfoDO::getId, id)
                .eq(ApplicationInfoDO::getAppTag, tag);
        boolean present = DataQuery.of(applicationInfoDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.TAG_PRESENT);
    }

}
